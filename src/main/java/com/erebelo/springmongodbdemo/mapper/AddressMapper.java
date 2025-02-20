package com.erebelo.springmongodbdemo.mapper;

import static org.mapstruct.ReportingPolicy.WARN;

import com.erebelo.springmongodbdemo.domain.entity.AddressEntity;
import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.domain.response.AddressResponse;
import com.erebelo.springmongodbdemo.domain.response.BulkErrorAddressResponse;
import com.erebelo.springmongodbdemo.util.HttpHeadersUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN, imports = {HttpHeadersUtil.class})
public interface AddressMapper {

    List<AddressEntity> requestListToEntityList(List<AddressRequest> requestList, @Context LocalDateTime dateTime);

    @Mapping(target = "createdBy", expression = "java(HttpHeadersUtil.getLoggedInUser())")
    @Mapping(target = "modifiedBy", expression = "java(HttpHeadersUtil.getLoggedInUser())")
    @Mapping(target = "createdDateTime", expression = "java(dateTime)")
    @Mapping(target = "modifiedDateTime", expression = "java(dateTime)")
    @Mapping(target = "version", constant = "0L")
    AddressEntity requestToEntity(AddressRequest request, @Context LocalDateTime dateTime);

    BulkErrorAddressResponse entityToBulkErrorAddressResponse(AddressEntity entity);

    List<AddressResponse> entityListToResponseList(List<AddressEntity> entityList);

    AddressResponse entityToResponse(AddressEntity entity);

}
