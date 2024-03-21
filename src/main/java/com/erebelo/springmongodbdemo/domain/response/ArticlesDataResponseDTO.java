package com.erebelo.springmongodbdemo.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "ArticlesDataResponseDTO")
public class ArticlesDataResponseDTO {

    private String title;
    private String url;
    private String author;
    private Integer numComments;
    private Integer storyId;
    private String storyTitle;
    private String storyUrl;
    private Integer parentId;
    private Integer createdAt;

}
