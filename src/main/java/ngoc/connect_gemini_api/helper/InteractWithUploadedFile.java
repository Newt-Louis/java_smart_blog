package ngoc.connect_gemini_api.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class InteractWithUploadedFile {
    @Value("${app.base-url}")
    private String configuredBaseUrl;

    /* Kiểm tra image file */
    public boolean isImageFile(MultipartFile file) {
        if (file == null) {
            return false;
        }
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public boolean isPdfFile(MultipartFile file) {
        if (file == null) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && contentType.equalsIgnoreCase("application/pdf");
    }

    /* In thông tin file ra console */
    public void printFileInfo(MultipartFile file) {
        if (file == null) {
            System.out.println("⚠️ File null");
            return;
        }
        System.out.println("📄 File name: " + file.getOriginalFilename());
        System.out.println("📦 Content type: " + file.getContentType());
        System.out.println("📏 Size: " + file.getSize() + " bytes");
    }

    /**
     * Tạo absolute URL từ đường dẫn tương đối.
     *
     * Nếu đang trong HTTP request → tự lấy base URL từ request.
     * Nếu không (ví dụ: trong @Async hoặc @Scheduled) → dùng base URL từ config.
     *
     * @param relativePath Đường dẫn tương đối (ví dụ "/uploads/temp/abc.png")
     * @return Absolute URL (ví dụ "http://localhost:8080/uploads/temp/abc.png")
     */
    public String getAbsoluteUrl(String relativePath) {
        String baseUrl = getCurrentBaseUrl();
        if (relativePath.startsWith("/")) {
            return baseUrl + relativePath.substring(1);
        }
        return baseUrl + relativePath;
    }

    private String getCurrentBaseUrl() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                return ServletUriComponentsBuilder.fromRequestUri(request)
                        .replacePath(null)
                        .build()
                        .toUriString()
                        .replaceAll("/$", "") + "/";
            }
        } catch (Exception ignored) { }
        return configuredBaseUrl.endsWith("/") ? configuredBaseUrl : configuredBaseUrl + "/";
    }
}

