package com.metuncc.mlm.datas;

import com.metuncc.mlm.api.request.BookRequest;
import com.metuncc.mlm.api.request.CreateRoomRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.dto.ImageDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.entity.enums.Department;
import com.metuncc.mlm.entity.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DTOSHelper {

    public UserDTO userDTO(){
        User user = new User();
        user.setPassword("1234");
        user.setRole(Role.USER);
        user.setUsername("username");
        user.setFullName("full name");
        user.setStudentNumber("1234567");
        user.setId(1L);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setDeleted(false);
        user.setDeletedDate(null);
        return user.toDTO();
    }
    public ShelfDTO shelfDTO(){
        Shelf shelf = new Shelf();
        shelf.setFloor("1");
        shelf.setId(1L);
        shelf.setCreatedDate(LocalDateTime.now());
        shelf.setLastModifiedDate(LocalDateTime.now());
        shelf.setDeleted(false);
        shelf.setDeletedDate(null);
        return shelf.toDTO();
    }

    public ImageDTO imageDTO(){
        Image image = new Image();
        image.setImageData(new byte[1]);
        image.setName("name");
        image.setType("type");
        image.setId(1L);
        image.setCreatedDate(LocalDateTime.now());
        image.setLastModifiedDate(LocalDateTime.now());
        image.setDeleted(false);
        image.setDeletedDate(null);
        return image.toDTO();
    }
    public BookDTO bookDTO(){
        Book book = new Book();
        book.setShelfId(new Shelf());
        book.setImageId(new Image());
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
        return book.toDTO();

    }

    public UserRequest userRequest1(){
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("bartu");
        userRequest.setNameSurname("Bartu Can Palamut");
        userRequest.setPass("VerySecurePassword");
        userRequest.setEmail("e238622@metu.edu.tr");
        userRequest.setStudentNumber("2386225");
        userRequest.setDepartment(Department.CNG);
        userRequest.setNameSurname("a a");
        return userRequest;
    }

    public BookRequest getBookRequest1() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setName("name");
        bookRequest.setIsbn("isbn");
        bookRequest.setAuthor("author");
        bookRequest.setCategory(BookCategory.FICTION);
        bookRequest.setShelfId(1L);
        bookRequest.setImageId(1L);
        bookRequest.setBarcode("barcode");
        bookRequest.setDescription("description");
        bookRequest.setEdition("edition");
        bookRequest.setPublisher("publisher");
        return bookRequest;
    }

    public CreateRoomRequest getCreateRoomRequest1() {
        CreateRoomRequest createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setName("1");
        createRoomRequest.setImageId(1L);
        return createRoomRequest;
    }
}
