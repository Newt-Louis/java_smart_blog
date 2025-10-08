package ngoc.connect_gemini_api.repository;

import org.apache.ibatis.annotations.Mapper;
import ngoc.connect_gemini_api.model.UploadedFile;

@Mapper
public interface UploadedFileMapper {
    void insertFile(UploadedFile uploadedFile);
}