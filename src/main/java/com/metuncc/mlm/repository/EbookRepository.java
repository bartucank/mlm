package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Ebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EbookRepository extends JpaRepository<Ebook,Long> {
    @Query("SELECT e FROM Ebook e WHERE e.book.id = :bookId")
    Ebook findByBookId(@Param("bookId") Long bookId);

}
