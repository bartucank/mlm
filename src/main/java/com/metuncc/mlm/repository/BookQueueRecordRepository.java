package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.BookQueueRecord;
import com.metuncc.mlm.entity.enums.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookQueueRecordRepository extends JpaRepository<BookQueueRecord,Long>, JpaSpecificationExecutor<Book> {

    @Query("select b from BookQueueRecord b where b.bookId=:book  and b.status=:status")
    BookQueueRecord getBookQueueRecordByBookIdAndDeletedAndStatus(@Param("book")Book book,@Param("status") QueueStatus status);

    @Query("select count(b) from BookQueueRecord b where b.status=:status")
    Integer getBookQueueRecordByStatus(@Param("status") QueueStatus status);

}
