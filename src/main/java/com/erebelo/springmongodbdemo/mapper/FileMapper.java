package com.erebelo.springmongodbdemo.mapper;

import static org.mapstruct.ReportingPolicy.WARN;

import com.erebelo.springmongodbdemo.domain.entity.FileEntity;
import com.erebelo.springmongodbdemo.domain.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface FileMapper {

    FileResponse entityToResponse(FileEntity entity);

    @Mapping(target = "name", source = "filename")
    @Mapping(target = "data", source = "dataBytes")
    FileEntity objToEntity(String filename, byte[] dataBytes);

}
