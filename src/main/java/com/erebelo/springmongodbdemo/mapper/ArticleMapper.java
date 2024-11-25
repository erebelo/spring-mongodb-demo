package com.erebelo.springmongodbdemo.mapper;

import static org.mapstruct.ReportingPolicy.WARN;

import com.erebelo.springmongodbdemo.domain.response.ArticleDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticleDataResponseDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface ArticleMapper {

    List<ArticleDataResponseDTO> responseToResponseDTO(List<ArticleDataResponse> response);

}
