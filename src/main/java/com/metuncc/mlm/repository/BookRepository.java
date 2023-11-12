package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    @Query("select b from Book b where b.shelfId.id=:shelfId")
    List<Book> getBooksByShelfId(@Param("shelfId")Long shelfId);


}
