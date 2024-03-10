package com.metuncc.mlm.repository;

import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.repository.specifications.BookSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long>, JpaSpecificationExecutor<Book> {
    @Query("select b from Book b where b.shelfId.id=:shelfId")
    List<Book> getBooksByShelfId(@Param("shelfId")Long shelfId);

    @Query("select count(b) from Book b")
    Integer totalBookCount();

    @Query("select count(b) from Book b where b.status=:status")
    Integer bookCountByAvailability(@Param("status") BookStatus status);

}
