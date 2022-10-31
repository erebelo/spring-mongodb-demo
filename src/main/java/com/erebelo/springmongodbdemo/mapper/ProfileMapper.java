package com.erebelo.springmongodbdemo.mapper;

import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface ProfileMapper {
}
