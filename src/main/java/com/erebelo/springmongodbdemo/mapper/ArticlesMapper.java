package com.erebelo.springmongodbdemo.mapper;

import static org.mapstruct.ReportingPolicy.WARN;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface ArticlesMapper {

    List<ArticlesDataResponseDTO> responseToResponseDTO(List<ArticlesDataResponse> response);

}
