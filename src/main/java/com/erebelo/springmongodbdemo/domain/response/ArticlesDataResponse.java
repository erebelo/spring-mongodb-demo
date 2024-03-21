package com.erebelo.springmongodbdemo.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(name = "ArticlesResponse")
public class ArticlesDataResponse {

    private String title;

    private String url;

    private String author;

    @JsonProperty("num_comments")
    private Integer numComments;

    @JsonProperty("story_id")
    private Integer storyId;

    @JsonProperty("story_title")
    private String storyTitle;

    @JsonProperty("story_url")
    private String storyUrl;

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("created_at")
    private Integer createdAt;

}
