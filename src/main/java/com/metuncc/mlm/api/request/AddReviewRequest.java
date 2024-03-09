package com.metuncc.mlm.api.request;

import lombok.Data;

@Data
public class AddReviewRequest {
    private Long star;
    private String comment;
    private Long bookId;
}
