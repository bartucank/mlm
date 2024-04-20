package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "favorite")
public class Favorite extends MLMBaseClass {

    @ManyToOne
    private User userId;
    @ManyToOne
    private Book bookId;

    public BookDTO toBookDTO() {
        return this.bookId.toDTO();
    }



}
