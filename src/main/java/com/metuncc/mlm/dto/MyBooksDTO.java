package com.metuncc.mlm.dto;

import lombok.Data;

@Data
public class MyBooksDTO {
    private BookDTO book;
    private Long days;
    private Boolean isLate;

}
