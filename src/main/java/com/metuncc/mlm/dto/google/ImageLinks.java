package com.metuncc.mlm.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageLinks {
    @JsonProperty("smallThumbnail")
    private String smallThumbnail;
    @JsonProperty("thumbnail")
    private String thumbnail;

}
