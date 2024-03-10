package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.BookBorrowHistory;
import com.metuncc.mlm.entity.BookReview;
import com.metuncc.mlm.entity.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview,Long>{


    @Query("select b from BookReview b where b.bookId.id=:bookId and b.userId.id=:userId")
    BookReview getByBookAndUserId(@Param("bookId") Long bookId, @Param("userId") Long userId);

    @Query("select r from BookReview r where r.bookId.id=:id")
    Page<BookReview> getByBookId(@Param("id") Long id, Pageable pageable);

    @Query("select AVG(b.star) from BookReview b where b.bookId.id=:id")
    BigDecimal getAvgByBookId(@Param("id") Long id);
}
