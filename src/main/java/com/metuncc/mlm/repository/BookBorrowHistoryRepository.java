package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.BookBorrowHistory;
import com.metuncc.mlm.entity.BookQueueRecord;
import com.metuncc.mlm.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookBorrowHistoryRepository extends JpaRepository<BookBorrowHistory,Long>, JpaSpecificationExecutor<Book> {


    @Query("select b from BookBorrowHistory b where b.createdDate<=:localDateTime and b.status=:borrowStatus")
    List<BookBorrowHistory> getBookBorrowHistoriesByStatusAndDate(@Param("localDateTime") LocalDateTime localDateTime,
                                                                  @Param("borrowStatus") BorrowStatus borrowStatus);
}
