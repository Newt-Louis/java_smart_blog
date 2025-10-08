package ngoc.connect_gemini_api.controller;

import ngoc.connect_gemini_api.dto.response.ApiResponse;
import ngoc.connect_gemini_api.service.FileSystemStorageService;
import ngoc.connect_gemini_api.helper.InteractWithUploadedFile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {
    private final FileSystemStorageService storageService;
    private final InteractWithUploadedFile fileHelper;

    public UploadController(FileSystemStorageService storageService,  InteractWithUploadedFile fileHelper) {
        this.storageService = storageService;
        this.fileHelper = fileHelper;
    }

    @PostMapping("/upload/images")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (!fileHelper.isImageFile(file)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Not an image file!"));
        }
        try {
            String tempFilename = storageService.storeTempFile(file);
            String fileUrl = fileHelper.getAbsoluteUrl("/uploads/temp/" + tempFilename);
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Could not upload the file: " + e.getMessage()));
        }
    }
}
