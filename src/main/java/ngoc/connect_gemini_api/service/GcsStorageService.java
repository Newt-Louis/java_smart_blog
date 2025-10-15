package ngoc.connect_gemini_api.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import ngoc.connect_gemini_api.model.UploadedFile;
import ngoc.connect_gemini_api.repository.UploadedFileMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Service
public class GcsStorageService {

    private final Storage storage;
    private final String bucketName;
    private final UploadedFileMapper uploadedFileMapper;

    @Autowired
    public GcsStorageService(Storage storage,
                             @Value("${gcp.bucket.name}") String bucketName,
                             UploadedFileMapper uploadedFileMapper) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.uploadedFileMapper = uploadedFileMapper;
    }

    /**
     * Lưu file tạm thời lên GCS vào "thư mục" temp.
     * Trả về URL công khai của file đó.
     *
     * @param file file người dùng upload
     * @return URL đầy đủ đến file trên GCS
     */
    public String storeTempFile(MultipartFile file) {
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if (file.isEmpty() || originalFilename.contains("..")) {
                throw new RuntimeException("Invalid file provided.");
            }

            String baseName = com.google.common.io.Files.getNameWithoutExtension(originalFilename);
            String extension = com.google.common.io.Files.getFileExtension(originalFilename);
            String randomChars = UUID.randomUUID().toString().substring(0, 5);
            String newFileName = baseName + "_" + randomChars + "." + extension;

            // Đường dẫn object trên GCS sẽ có dạng: temp/my-image_abc12.png
            String gcsObjectPath = "temp/" + newFileName;

            BlobId blobId = BlobId.of(bucketName, gcsObjectPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            // Upload file lên GCS
            Blob blob = storage.create(blobInfo, file.getBytes());

            // Trả về URL công khai. Lưu ý: Bucket của bạn phải được cấu hình public.
            return blob.getMediaLink(); // Hoặc dùng String.format("https://storage.googleapis.com/%s/%s", bucketName, gcsObjectPath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file in GCS", e);
        }
    }

    /**
     * Xử lý nội dung HTML, tìm các ảnh tạm trên GCS, "di chuyển" chúng đến thư mục của user
     * và cập nhật lại đường dẫn trong HTML.
     *
     * @param htmlContent nội dung HTML từ Quill Editor
     * @param userId      ID người dùng đang đăng nhập
     * @return string HTML với các thẻ <img> đã biến đổi src thành URL GCS mới.
     */
    public String processAndCommitImages(String htmlContent, int userId) {
        String tempUrlPattern = String.format("img[src*=%s/temp/]", bucketName);
        Document document = Jsoup.parseBodyFragment(htmlContent);
        Elements images = document.select(tempUrlPattern);

        for (Element image : images) {
            String tempUrl = image.attr("src");
            String tempFileName = tempUrl.substring(tempUrl.lastIndexOf("/") + 1);
            String tempGcsObjectPath = "temp/" + tempFileName;

            // Đường dẫn mới cho file, ví dụ: user-files/123/my-image_abc12.png
            String userFolder = String.valueOf(userId);
            String newGcsObjectPath = userFolder + "/" + tempFileName;

            try {
                BlobId sourceBlobId = BlobId.of(bucketName, tempGcsObjectPath);
                Blob sourceBlob = storage.get(sourceBlobId);

                if (sourceBlob != null && sourceBlob.exists()) {
                    BlobId targetBlobId = BlobId.of(bucketName, newGcsObjectPath);
                    Blob copiedBlob = sourceBlob.copyTo(targetBlobId).getResult();
                    sourceBlob.delete();

                    String newUrl = copiedBlob.getMediaLink();
                    image.attr("src", newUrl);

                    String baseName = com.google.common.io.Files.getNameWithoutExtension(tempFileName);
                    String extension = com.google.common.io.Files.getFileExtension(tempFileName);
                    int lastUnderscoreIndex = baseName.lastIndexOf('_');
                    String originalFilename = baseName.substring(0, lastUnderscoreIndex) + "." + extension;

                    UploadedFile fileInfo = new UploadedFile();
                    fileInfo.setFilename(tempFileName);
                    fileInfo.setOriginalFilename(originalFilename);
                    fileInfo.setFilePath(newUrl); // QUAN TRỌNG: Lưu URL đầy đủ vào DB
                    fileInfo.setMimeType(copiedBlob.getContentType());
                    fileInfo.setFileSize(copiedBlob.getSize());
                    fileInfo.setUserId(userId);
                    uploadedFileMapper.insertFile(fileInfo);
                }
            } catch (Exception e) {
                System.err.println("Failed to process temp file from GCS: " + tempFileName + " Error: " + e.getMessage());
            }
        }

        return document.body().html();
    }
}