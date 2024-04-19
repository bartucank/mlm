package com.metuncc.mlm.dto.ml;

import lombok.Data;

@Data
public class LightReview {
    private Long id;
    private String comment;
    private Long rating;
    private Long userId;
}
