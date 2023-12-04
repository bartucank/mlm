package com.metuncc.mlm.api.request;

import com.metuncc.mlm.entity.enums.BookStatus;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryRequest {
    private Long id;
    private Long userID;
    private Long bookID;
    private LocalDateTime returnDate;
    private BookStatus status;
}
