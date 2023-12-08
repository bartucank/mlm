package com.metuncc.mlm.entity;


import com.metuncc.mlm.api.request.BookRequest;
import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.entity.enums.QueueStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class BookQueueRecord extends MLMBaseClass {

    @ManyToOne
    private Book bookId;

    @Enumerated(value = EnumType.STRING)
    private QueueStatus status;

    private LocalDateTime completeDate;


    @OneToMany(mappedBy = "bookQueueRecord", cascade = CascadeType.ALL)
    private List<BookBorrowHistory> bookBorrowHistoryList;

    public void updateBookBorrow(BookBorrowHistory bookBorrowHistory){
        if (bookBorrowHistoryList != null && !bookBorrowHistoryList.isEmpty()) {
            for (int i = 0; i < bookBorrowHistoryList.size(); i++) {
                BookBorrowHistory currentBookBorrowHistory = bookBorrowHistoryList.get(i);
                if (currentBookBorrowHistory.getId().equals(bookBorrowHistory.getId())) {
                    bookBorrowHistoryList.set(i, bookBorrowHistory);
                    break;
                }
            }
        }
    }

}
