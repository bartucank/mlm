package com.metuncc.mlm.dto;

import lombok.Data;

@Data
public class CourseMaterialDTO {
    private Long id;
    private String name;
    private byte[] data;
    private String fileName;
    private String extension;
}
