package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.response.ArticleDataResponseDTO;
import java.util.List;

public interface ArticleService {

    List<ArticleDataResponseDTO> getArticles();

}
