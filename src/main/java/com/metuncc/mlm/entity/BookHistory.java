package com.metuncc.mlm.entity;

import com.metuncc.mlm.api.request.HistoryRequest;
import com.metuncc.mlm.dto.BorrowHistoryDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.BookStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "bookhistory")
public class BookHistory extends MLMBaseClass {
    @ManyToMany
    private User userId;
    @ManyToMany
    private Book bookId;
    @OneToOne
    private LocalDateTime returnDate;
    @Enumerated(value = EnumType.STRING)
    private BookStatus status;


   public BorrowHistoryDTO toDTO(){
       BorrowHistoryDTO borrowHistoryDTO = new BorrowHistoryDTO();
       borrowHistoryDTO.setUserId(getUserId());
       borrowHistoryDTO.setBookId(getBookId());
       borrowHistoryDTO.setReturnDate(getReturnDate());
       borrowHistoryDTO.setStatus(getStatus());
       borrowHistoryDTO.setStatusStr(getStatus().toString());

       return borrowHistoryDTO;
   }

   public BookHistory fromRequest(HistoryRequest historyRequest){
        setReturnDate(historyRequest.getReturnDate());
        setStatus(historyRequest.getStatus());

        return this;
   }


}
