package com.erebelo.springmongodbdemo.mapper;

import static org.mapstruct.ReportingPolicy.WARN;

import com.erebelo.springmongodbdemo.domain.entity.AddressEntity;
import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.domain.response.AddressResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface AddressMapper {

    @Mapping(target = "createdBy", expression = "java(applicationName)")
    @Mapping(target = "modifiedBy", expression = "java(applicationName)")
    @Mapping(target = "createdDateTime", expression = "java(dateTime)")
    @Mapping(target = "modifiedDateTime", expression = "java(dateTime)")
    @Mapping(target = "version", constant = "0L")
    AddressEntity requestToEntity(AddressRequest request, String applicationName, LocalDateTime dateTime);

    List<AddressResponse> entityListToResponseList(List<AddressEntity> entityList);

    AddressResponse entityToResponse(AddressEntity entity);

}
