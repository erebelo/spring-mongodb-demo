package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.FILES;
import static com.erebelo.springmongodbdemo.util.HttpHeadersUtil.getFileApiResponseHeaders;

import com.erebelo.springmongodbdemo.domain.response.FileResponseDTO;
import com.erebelo.springmongodbdemo.service.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Log4j2
@RestController
@RequestMapping(FILES)
@RequiredArgsConstructor
public class FileController {

    private final FileService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileResponseDTO>> getFiles() {
        log.info("GET {}", FILES);
        return ResponseEntity.ok(service.getFiles());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFileById(@PathVariable String id) {
        log.info("GET {}/{}", FILES, id);
        var response = service.getFileById(id);

        return ResponseEntity.ok().headers(getFileApiResponseHeaders(response.getName(), response.getData().length))
                .body(response.getData());
    }

    /**
     * Uploads a single file. Supports files up to 16MB in size.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("POST {} - fileName={}, fileSize={} bytes, contentType={}", FILES, file.getOriginalFilename(),
                file.getSize(), file.getContentType());
        String fileId = service.uploadFile(file);

        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(fileId).toUri())
                .build();
    }
}
