package com.metuncc.mlm.dto.ml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MLRecommends
{

    @JsonProperty("recommendations")
    private Long[] recommendations;
}
