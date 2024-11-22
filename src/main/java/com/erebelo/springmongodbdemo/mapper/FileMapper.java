package com.erebelo.springmongodbdemo.mapper;

import static org.mapstruct.ReportingPolicy.WARN;

import com.erebelo.springmongodbdemo.domain.entity.FileDocument;
import com.erebelo.springmongodbdemo.domain.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface FileMapper {

    FileResponse documentToResponse(FileDocument document);

    @Mapping(target = "name", source = "filename")
    @Mapping(target = "data", source = "dataBytes")
    FileDocument fileToDocument(String filename, byte[] dataBytes);

}
