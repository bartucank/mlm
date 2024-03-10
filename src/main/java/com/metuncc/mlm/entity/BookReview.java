package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.BookReviewDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class BookReview extends MLMBaseClass {
    @ManyToOne
    private Book bookId;
    @ManyToOne
    private User userId;
    private Long star;
    private String comment;


    public BookReviewDTO toDTO(){
        BookReviewDTO dto = new BookReviewDTO();
        dto.setUserId(getUserId().getId());
        dto.setStar(getStar());
        dto.setComment(getComment());
        return dto;
    }

}