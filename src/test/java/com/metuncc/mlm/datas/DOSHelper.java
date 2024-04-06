package com.metuncc.mlm.datas;

import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DOSHelper {


    public User user1(){
        User user = new User();
        user.setPassword("1234");
        user.setRole(Role.USER);
        user.setUsername("username");
        user.setEmail("a@metu.edu.tr");
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

    public Room room1(){
        Room room = new Room();
        room.setId(1L);
        room.setImageId(image1());
        room.setQrImage(image1());
        room.setNFC_no("nfc no");
        room.setCreatedDate(LocalDateTime.now());
        room.setLastModifiedDate(LocalDateTime.now());
        room.setDeleted(false);
        room.setDeletedDate(null);
        return room;
    }

    public RoomSlot roomSlot1(){
        RoomSlot roomSlot = new RoomSlot();
        roomSlot.setId(1L);
        roomSlot.setStartHour(LocalDateTime.now().toLocalTime());
        roomSlot.setEndHour(LocalDateTime.now().plusHours(1L).toLocalTime());
        roomSlot.setDay(RoomSlotDays.MON);
        roomSlot.setAvailable(true);
        roomSlot.setRoom(room1());
        roomSlot.setCreatedDate(LocalDateTime.now());
        roomSlot.setLastModifiedDate(LocalDateTime.now());
        roomSlot.setDeleted(false);
        roomSlot.setDeletedDate(null);
        return roomSlot;
    }

    public RoomReservation reservation1(){
        RoomReservation reservation = new RoomReservation();
        reservation.setId(1L);
        reservation.setRoomSlot(roomSlot1());
        reservation.setUserId(user1().getId());
        reservation.setCreatedDate(LocalDateTime.now());
        reservation.setLastModifiedDate(LocalDateTime.now());
        reservation.setDate(LocalDate.now());
        reservation.setDeleted(false);
        reservation.setDeletedDate(null);
        reservation.setApproved(false);
        return reservation;
    }
}
