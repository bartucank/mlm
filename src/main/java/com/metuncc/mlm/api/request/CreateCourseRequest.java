package com.metuncc.mlm.api.request;

import lombok.Data;

@Data
public class CreateCourseRequest {
    private String name;
    private Boolean isPublic;
    private Long imageId;
}
