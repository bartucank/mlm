package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.BookBorrowHistory;
import com.metuncc.mlm.entity.BookQueueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookBorrowHistoryRepository extends JpaRepository<BookBorrowHistory,Long>, JpaSpecificationExecutor<Book> {


}
