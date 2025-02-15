package com.erebelo.springmongodbdemo.service.impl;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_006;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_007;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_409_002;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_003;

import com.erebelo.springmongodbdemo.domain.entity.FileEntity;
import com.erebelo.springmongodbdemo.domain.response.FileResponse;
import com.erebelo.springmongodbdemo.domain.response.FileResponseDTO;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.FileMapper;
import com.erebelo.springmongodbdemo.repository.FileRepository;
import com.erebelo.springmongodbdemo.repository.projection.FileEntityProjection;
import com.erebelo.springmongodbdemo.service.FileService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMapper mapper;
    private final FileRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<FileResponseDTO> getFiles() {
        log.info("Fetching all files");
        List<FileEntityProjection> fileProjections = repository.findAllFileEntityProjection();

        if (fileProjections.isEmpty()) {
            throw new CommonException(COMMON_ERROR_404_006);
        }

        log.info("{} files found", fileProjections.size());
        return fileProjections.stream().map(fp -> new FileResponseDTO(fp.getId(), fp.getName())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FileResponse getFileById(String id) {
        log.info("Fetching file with id: {}", id);
        FileEntity file = repository.findById(id).orElseThrow(() -> new CommonException(COMMON_ERROR_404_007, id));

        log.info("File successfully retrieved: {}", file.getName());
        return mapper.entityToResponse(file);
    }

    @Override
    @Transactional
    public String uploadFile(MultipartFile multipartFile) {
        log.info("Uploading file");

        String filename = multipartFile.getOriginalFilename();
        byte[] dataBytes = extractFileBytes(multipartFile);

        if (dataBytes.length == 0 || !isValidFilename(filename)) {
            throw new CommonException(COMMON_ERROR_422_003);
        }

        log.info("Checking if file already exists by name");
        repository.findByName(filename).ifPresent(o -> {
            throw new CommonException(COMMON_ERROR_409_002, filename);
        });

        log.info("Inserting file");
        String fileId = repository.save(mapper.objToEntity(filename, dataBytes)).getId();

        log.info("File uploaded successfully: filename={}, fileId={}", filename, fileId);
        return fileId;
    }

    private byte[] extractFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Error processing file: %s", file.getName()), e);
        }
    }

    private boolean isValidFilename(String filename) {
        try {
            String[] breakdown = filename.split("\\.");
            return breakdown.length == 2 && !ObjectUtils.isEmpty(breakdown[0]);
        } catch (Exception e) {
            log.warn("An exception occurred while validating the file: {}", filename, e);
            return false;
        }
    }
}
