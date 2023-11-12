package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.api.request.BookRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "book")
public class Book extends MLMBaseClass {

    @ManyToOne
    private Shelf shelfId;
    @OneToOne
    private Image imageId;
    private String isbn;
    private String publisher;
    private String name;
    private String description;
    private String author;
    private LocalDate publicationDate;
    private String edition;
    private String barcode;
    @Enumerated(value = EnumType.STRING)
    private BookCategory category;
    @Enumerated(value = EnumType.STRING)
    private BookStatus status;



    public BookDTO toDTO() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setShelfId(getShelfId());
        bookDTO.setImageId(getImageId());
        bookDTO.setIsbn(getIsbn());
        bookDTO.setPublisher(getPublisher());
        bookDTO.setName(getName());
        bookDTO.setDescription(getDescription());
        bookDTO.setAuthor(getAuthor());
        bookDTO.setPublicationDate(getPublicationDate());
        bookDTO.setEdition(getEdition());
        bookDTO.setBarcode(getBarcode());
        bookDTO.setCategory(getCategory());
        bookDTO.setCategoryStr(getCategory().toString());
        bookDTO.setStatus(getStatus());
        bookDTO.setStatusStr(getStatus().toString());
        return bookDTO;
    }


    public Book fromRequest(BookRequest bookRequest) {


        setIsbn(bookRequest.getIsbn());
        setPublisher(bookRequest.getPublisher());
        setName(bookRequest.getName());
        setDescription(bookRequest.getDescription());
        setAuthor(bookRequest.getAuthor());
        setPublicationDate(bookRequest.getPublicationDate());
        setEdition(bookRequest.getEdition());
        setBarcode(bookRequest.getBarcode());
        setCategory(bookRequest.getCategory());
        setStatus(bookRequest.getStatus());

        return this;
    }
}
