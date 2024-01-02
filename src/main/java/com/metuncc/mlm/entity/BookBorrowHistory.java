package com.metuncc.mlm.entity;


import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.BorrowStatus;
import com.metuncc.mlm.entity.enums.QueueStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class BookBorrowHistory extends MLMBaseClass {

    @Enumerated(value = EnumType.STRING)
    private BorrowStatus status;

    @ManyToOne
    private User userId;

    @ManyToOne
    @JoinColumn(name = "book_queue_record_id")
    private BookQueueRecord bookQueueRecord;

    private LocalDateTime returnDate;

    private LocalDateTime takeDate;


    @Override
    public String toString(){
        return "";
    }
}
