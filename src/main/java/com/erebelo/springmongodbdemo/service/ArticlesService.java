package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import java.util.List;

public interface ArticlesService {

    List<ArticlesDataResponseDTO> getArticles();

}
