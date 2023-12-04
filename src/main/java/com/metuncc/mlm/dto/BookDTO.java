package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.entity.enums.Role;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper=true)
public class BookDTO extends MLMBaseClassDTO {

    private Long shelfId;
    private Long imageId;
    private String isbn;
    private String publisher;
    private String name;
    private String description;
    private String author;
    private LocalDate publicationDate;
    private LocalDate publicationDateStr;
    private String edition;
    private String barcode;
    private BookCategory category;
    private String categoryStr;
    private BookStatus status;
    private String statusStr;


}
