package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.BookHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookHistoryRepository extends JpaRepository<BookHistory,Long>, JpaSpecificationExecutor<BookHistory> {
    @Query("select b from BookHistory b where b.bookId.id=:bookId")
    List<BookHistory> getBookHistoryByBookId(@Param("bookId")Long bookId);

    @Query("select s from BookHistory s where s.userId.id=:userId")
    List<BookHistory> getBookHistoryByUserId(@Param("userId")Long userId);
}
