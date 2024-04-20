package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.dto.BookReviewDTO;
import com.metuncc.mlm.dto.ml.LightBook;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.api.request.BookRequest;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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
    @Column(length = 2000)
    private String description;
    private String author;
    private LocalDate publicationDate;
    private String edition;
    private String barcode;
    @Enumerated(value = EnumType.STRING)
    private BookCategory category;
    @Enumerated(value = EnumType.STRING)
    private BookStatus status;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
    private Ebook ebook;




    public BookDTO toDTO() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(getId());
        bookDTO.setShelfId(getShelfId().getId());
        bookDTO.setImageId(getImageId().getId());
        bookDTO.setIsbn(getIsbn());
        bookDTO.setPublisher(getPublisher());
        bookDTO.setName(getName());
        bookDTO.setDescription(getDescription());
        bookDTO.setAuthor(getAuthor());
        bookDTO.setPublicationDate(getPublicationDate());
        bookDTO.setPublicationDateStr(getPublicationDate());
        bookDTO.setEdition(getEdition());
        bookDTO.setBarcode(getBarcode());
        bookDTO.setCategory(getCategory());
        bookDTO.setCategoryStr(getCategory().toString());
        bookDTO.setStatus(getStatus());
        bookDTO.setStatusStr(getStatus().toString());
        bookDTO.setEbookId(getEbook().getId());
        return bookDTO;
    }
    public BookDTO toDTOWithReview(BookReviewDTO dto) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(getId());
        bookDTO.setShelfId(getShelfId().getId());
        bookDTO.setImageId(getImageId().getId());
        bookDTO.setIsbn(getIsbn());
        bookDTO.setPublisher(getPublisher());
        bookDTO.setName(getName());
        bookDTO.setDescription(getDescription());
        bookDTO.setAuthor(getAuthor());
        bookDTO.setPublicationDate(getPublicationDate());
        bookDTO.setPublicationDateStr(getPublicationDate());
        bookDTO.setEdition(getEdition());
        bookDTO.setBarcode(getBarcode());
        bookDTO.setCategory(getCategory());
        bookDTO.setCategoryStr(getCategory().toString());
        bookDTO.setStatus(getStatus());
        bookDTO.setStatusStr(getStatus().toString());
        bookDTO.setReviewDTO(dto);
        bookDTO.setEbookId(getEbook().getId());
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
    public Book fromRequestUpdate(BookRequest bookRequest) {


        setPublisher(bookRequest.getPublisher());
        setName(bookRequest.getName());
        setDescription(bookRequest.getDescription());
        setAuthor(bookRequest.getAuthor());
        setPublicationDate(bookRequest.getPublicationDate());
        setEdition(bookRequest.getEdition());
        setBarcode(bookRequest.getBarcode());
        setCategory(bookRequest.getCategory());

        return this;
    }

    public LightBook toLightDTO() {
        LightBook lightBook = new LightBook();
        lightBook.setId(getId());
        lightBook.setTitle(getName());
        lightBook.setAuthor(getAuthor());
        lightBook.setDescription(getDescription());
        lightBook.setIsbn(getIsbn());
        return lightBook;
    }
}
