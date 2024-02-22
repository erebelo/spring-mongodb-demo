package com.erebelo.springmongodbdemo.mapper;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface ArticlesMapper {

    List<ArticlesDataResponseDTO> responseToResponseDTO(List<ArticlesDataResponse> response);

}

