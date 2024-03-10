package com.metuncc.mlm.dto.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeInfo {
    @JsonProperty("imageLinks")
    private ImageLinks imageLinks;

}
