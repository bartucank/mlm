package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.BookStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=true)
public class BorrowHistoryDTO extends MLMBaseClassDTO {
    private User userId;
    private Book bookId;
    private LocalDateTime returnDate;
    private BookStatus status;
    private String statusStr;
}
