package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.response.FileResponse;
import com.erebelo.springmongodbdemo.domain.response.FileResponseDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    List<FileResponseDTO> getFiles();

    FileResponse getFileById(String id);

    String uploadFile(MultipartFile file);

}
