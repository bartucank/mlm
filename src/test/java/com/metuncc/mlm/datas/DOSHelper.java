package com.metuncc.mlm.datas;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.entity.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DOSHelper {


    public User user1(){
        User user = new User();
        user.setPassword("1234");
        user.setRole(Role.USER);
        user.setUsername("username");
        user.setFullName("full name");
        user.setId(1L);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setDeleted(false);
        user.setDeletedDate(null);
        return user;
    }

    public Shelf shelf1(){
        Shelf shelf = new Shelf();
        shelf.setFloor("1");
        shelf.setId(1L);
        shelf.setCreatedDate(LocalDateTime.now());
        shelf.setLastModifiedDate(LocalDateTime.now());
        shelf.setDeleted(false);
        shelf.setDeletedDate(null);
        return shelf;
    }

    public Image image1(){
        Image image = new Image();
        image.setImageData(new byte[1]);
        image.setName("name");
        image.setType("type");
        image.setId(1L);
        image.setCreatedDate(LocalDateTime.now());
        image.setLastModifiedDate(LocalDateTime.now());
        image.setDeleted(false);
        image.setDeletedDate(null);
        return image;
    }
    public Book book1(){
        Book book = new Book();
        book.setShelfId(shelf1());
        book.setImageId(image1());
        book.setIsbn("1234");
        book.setPublisher("publisher name");
        book.setName("book title");
        book.setDescription("book description");
        book.setAuthor("author name");
        book.setPublicationDate(LocalDate.now());
        book.setEdition("edition");
        book.setBarcode("barcode");
        book.setCategory(BookCategory.FICTION);
        book.setStatus(BookStatus.AVAILABLE);
        book.setId(1L);
        book.setCreatedDate(LocalDateTime.now());
        book.setLastModifiedDate(LocalDateTime.now());
        book.setDeleted(false);
        book.setDeletedDate(null);
        return book;

    }
}
