package ngoc.connect_gemini_api.service;
import ngoc.connect_gemini_api.model.UploadedFile;
import ngoc.connect_gemini_api.repository.UploadedFileMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.apache.tika.Tika;

import jakarta.annotation.PostConstruct;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileSystemStorageService {
    private final Path rootLocation;
    private final UploadedFileMapper uploadedFileMapper;
    private final Tika tika = new Tika();

    public FileSystemStorageService(@Value("${file.upload-dir}") String uploadDir, UploadedFileMapper uploadedFileMapper) {
        this.rootLocation = Paths.get(uploadDir);
        this.uploadedFileMapper = uploadedFileMapper;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            Path guestDir = rootLocation.resolve("0");
            Path tempDir = rootLocation.resolve("temp");
            Files.createDirectories(guestDir);
            Files.createDirectories(tempDir);
            System.out.println("Storage initialized. Root folder and guest folder created!");
        } catch (IOException e) {
            throw new RuntimeException("Cannot inititalize storage folder",e);
        }
    }

    public String storeTempFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Cannot store file with relative path outside current directory " + originalFilename);
            }

            String baseName = com.google.common.io.Files.getNameWithoutExtension(originalFilename);
            String extension = com.google.common.io.Files.getFileExtension(originalFilename);
            String randomChars = UUID.randomUUID().toString().substring(0,5);
            String newFileName = baseName + "_" + randomChars + "." + extension;
            Path tempDirectory = rootLocation.resolve("temp");
            Path destinationFile = tempDirectory.resolve(newFileName).toAbsolutePath();

            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream,destinationFile,StandardCopyOption.REPLACE_EXISTING);
            }

            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xử lý nội dung HTML, tìm các ảnh tạm, di chuyển chúng đến thư mục của user
     * và cập nhật lại đường dẫn trong HTML.
     *
     * @param htmlContent nội dung HTML từ Quill Editor
     * @param userId ID người dùng đang đăng nhập nếu không có = 0
     * @return string HTML với các thẻ <img> đã biến đổi src mới.
     */
    public String processAndCommitImages(String htmlContent, int userId) {
        Document document = Jsoup.parseBodyFragment(htmlContent);

        Elements images = document.select("img[src*=/uploads/temp]");

        for (Element image : images) {
            String tempUrl = image.attr("src");
            String tempFileName = tempUrl.substring(tempUrl.lastIndexOf("/")+1);
            Path sourcePath = rootLocation.resolve("temp").resolve(tempFileName);
            String userFolder = String.valueOf(userId);
            Path destinationDir = rootLocation.resolve(userFolder);

            try {
                Files.createDirectories(destinationDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Path destinationPath =  destinationDir.resolve(tempFileName);

            try {
                if (Files.exists(sourcePath)){
                    long fileSize = Files.size(sourcePath);
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    String newUrl = "/uploads/" + userFolder + "/" + tempFileName;
                    image.attr("src", newUrl);

                    /* Tái tạo lại tên file gốc */
                    String baseName = com.google.common.io.Files.getNameWithoutExtension(tempFileName);
                    String extension = com.google.common.io.Files.getFileExtension(tempFileName);
                    int lastUnderscoreIndex = baseName.lastIndexOf('_');
                    String originalFilename = baseName.substring(0, lastUnderscoreIndex) + "." + extension;

                    /* Lấy và kiểm tra mimeType */
                    String mimeType = tika.detect(destinationPath);
                    if (mimeType == null || !mimeType.startsWith("image/")) {
                        Files.delete(destinationPath);
                        throw new IOException("File is not a valid image: " + tempFileName);
                    }

                    /* Lưu file vào database */
                    UploadedFile fileInfo = new UploadedFile();
                    fileInfo.setFilename(tempFileName);
                    fileInfo.setOriginalFilename(originalFilename);
                    fileInfo.setFilePath(newUrl);
                    fileInfo.setMimeType(mimeType);
                    fileInfo.setFileSize(fileSize);
                    fileInfo.setUserId(userId);
                    uploadedFileMapper.insertFile(fileInfo);
                }
            } catch (IOException e){
                System.err.println("Failed to move temp file: " + tempFileName + " Error: " + e.getMessage());
            }
        }

        return document.body().html();
    }

    public void createUserDirectory(int userId) {
        try {
            Path userDirectory = rootLocation.resolve(String.valueOf(userId));
            java.nio.file.Files.createDirectories(userDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory for user: " + userId, e);
        }
    }
}
