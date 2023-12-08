package com.metuncc.mlm.api.request;

import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.entity.enums.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FindBookRequest {
    private String name;
    private String author;
    private String publisher;
    private String description;
    private String isbn;
    private LocalDate publicationDate;
    private String barcode;

    private BookCategory category;
    private BookStatus status;

    private int page;
    private int size;

}
