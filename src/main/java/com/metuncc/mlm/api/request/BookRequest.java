package com.metuncc.mlm.api.request;

import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookRequest {
    private Long id;
    private Long shelfId;
    private Long imageId;
    private String isbn;
    private String publisher;
    private String name;
    private String description;
    private String author;
    private LocalDate publicationDate;
    private String edition;
    private String barcode;
    private BookCategory category;
    private BookStatus status;
}
