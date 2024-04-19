package com.metuncc.mlm.dto.ml;

import lombok.Data;

@Data
public class LightBook {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String isbn;
}
