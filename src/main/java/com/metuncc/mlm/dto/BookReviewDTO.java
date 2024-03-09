package com.metuncc.mlm.dto;

import lombok.Data;

@Data
public class BookReviewDTO {
    private Long userId;
    private Long star;
    private String comment;
}
