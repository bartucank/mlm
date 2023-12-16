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
    private Long shelfId; //Combobox
    private Long imageId;
    private String isbn; //ok
    private String publisher;//ok
    private String name;//ok
    private String description;//ok
    private String author;//ok
    private LocalDate publicationDate;//ok
    private String edition; //ignore
    private String barcode; //ignore
    private BookCategory category; //Combobox
    private BookStatus status;
}
