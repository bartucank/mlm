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

    @Query("select b from Book b where b.status=:status")
    List<Book> getByStatus(@Param("status")BookStatus status);

    @Query("select b from Book b where b.isbn=:isbn")
    List<Book> getBookByIsbn(@Param("isbn") String isbn);

    @Query("select b from Book b " +
            "where (:status is null or b.status in :status) and " +
            "(:shelfId is null or b.shelfId.id in :shelfId) and " +
            "(:author is null or lower(b.author) like :author) and " +
            "(:name is null or lower(b.name) like :name) and " +
            "(:publisher is null or lower(b.publisher) like :publisher) and " +
            "(:category is null or b.category = :category) and " +
            "(:ebook is null or ((:ebook = true and b.ebook is not null) or (:ebook = false and b.ebook is null)))")
    List<Book> getBooksByFilters(@Param("status") List<BookStatus> status,
                                 @Param("shelfId") List<Long> shelfId,
                                 @Param("author") String author,
                                 @Param("publisher") String publisher,
                                 @Param("name") String name,
                                 @Param("category") List<BookCategory> category,
                                 @Param("ebook") Boolean ebook);

    @Query("select distinct b.author from Book b " +
            "where (:status is null or b.status in :status) and " +
            "(:shelfId is null or b.shelfId.id in :shelfId) and " +
            "(:author is null or lower(b.author) like :author) and " +
            "(:name is null or lower(b.name) like :name) and " +
            "(:publisher is null or lower(b.publisher) like :publisher) and " +
            "(:category is null or b.category = :category) and " +
            "(:ebook is null or ((:ebook = true and b.ebook is not null) or (:ebook = false and b.ebook is null)))")
    List<String> getAuthors(@Param("status") List<BookStatus> status,
                            @Param("shelfId") List<Long> shelfId,
                            @Param("author") String author,
                            @Param("publisher") String publisher,
                            @Param("name") String name,
                            @Param("category") List<BookCategory> category,
                            @Param("ebook") Boolean ebook);

    @Query("select distinct b.shelfId.id from Book  b " +
            "where (:status is null or b.status in :status) and " +
            "(:shelfId is null or b.shelfId.id in :shelfId) and " +
            "(:author is null or lower(b.author) like :author) and " +
            "(:name is null or lower(b.name) like :name) and " +
            "(:publisher is null or lower(b.publisher) like :publisher) and " +
            "(:category is null or b.category = :category) and " +
            "(:ebook is null or ((:ebook = true and b.ebook is not null) or (:ebook = false and b.ebook is null)))")
    List<Long> getShelfs(@Param("status") List<BookStatus> status,
                         @Param("shelfId") List<Long> shelfId,
                         @Param("author") String author,
                         @Param("publisher") String publisher,
                         @Param("name") String name,
                         @Param("category") List<BookCategory> category,
                         @Param("ebook") Boolean ebook);

    @Query("select distinct b.publisher from Book b " +
            "where (:status is null or b.status in :status) and " +
            "(:shelfId is null or b.shelfId.id in :shelfId) and " +
            "(:author is null or lower(b.author) like :author) and " +
            "(:name is null or lower(b.name) like :name) and " +
            "(:publisher is null or lower(b.publisher) like :publisher) and " +
            "(:category is null or b.category = :category) and " +
            "(:ebook is null or ((:ebook = true and b.ebook is not null) or (:ebook = false and b.ebook is null)))")
    List<String> getPublishers(@Param("status") List<BookStatus> status,
                               @Param("shelfId") List<Long> shelfId,
                               @Param("author") String author,
                               @Param("publisher") String publisher,
                               @Param("name") String name,
                               @Param("category") List<BookCategory> category,
                               @Param("ebook") Boolean ebook);

    @Query("select distinct b.category from Book b " +
            "where (:status is null or b.status in :status) and " +
            "(:shelfId is null or b.shelfId.id in :shelfId) and " +
            "(:author is null or lower(b.author) like :author) and " +
            "(:name is null or lower(b.name) like :name) and " +
            "(:publisher is null or lower(b.publisher) like :publisher) and " +
            "(:category is null or b.category = :category) and " +
            "(:ebook is null or ((:ebook = true and b.ebook is not null) or (:ebook = false and b.ebook is null)))")
    List<BookCategory> getCategories(@Param("status") List<BookStatus> status,
                                     @Param("shelfId") List<Long> shelfId,
                                     @Param("author") String author,
                                     @Param("publisher") String publisher,
                                     @Param("name") String name,
                                     @Param("category") List<BookCategory> category,
                                     @Param("ebook") Boolean ebook);

    @Query("select distinct b.status from Book  b " +
            "where (:status is null or b.status in :status) and " +
            "(:shelfId is null or b.shelfId.id in :shelfId) and " +
            "(:author is null or lower(b.author) like :author) and " +
            "(:name is null or lower(b.name) like :name) and " +
            "(:publisher is null or lower(b.publisher) like :publisher) and " +
            "(:category is null or b.category = :category) and " +
            "(:ebook is null or ((:ebook = true and b.ebook is not null) or (:ebook = false and b.ebook is null)))")
    List<BookStatus> getStatuses(@Param("status") List<BookStatus> status,
                                 @Param("shelfId") List<Long> shelfId,
                                 @Param("author") String author,
                                 @Param("publisher") String publisher,
                                 @Param("name") String name,
                                 @Param("category") List<BookCategory> category,
                                 @Param("ebook") Boolean ebook);


    @Query("select count(b) from Book b where b.shelfId.id=:shelfId")
    Long getCountByShelfId(@Param("shelfId") Long shelfId);
}
