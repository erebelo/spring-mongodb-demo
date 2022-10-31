package com.erebelo.springmongodbdemo.mapper;

import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface ProfileMapper {

    ProfileResponse entityToResponse(ProfileEntity entity);

    ProfileEntity requestToEntity(ProfileRequest request);

}
