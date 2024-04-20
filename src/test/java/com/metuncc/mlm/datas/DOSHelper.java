package com.metuncc.mlm.datas;

import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DOSHelper {


    public User user1(){
        User user = new User();
        user.setPassword("1234");
        user.setRole(Role.USER);
        user.setUsername("username");
        user.setEmail("a@metu.edu.tr");
        user.setFullName("full name");
        user.setStudentNumber("1234567");
        user.setDepartment(Department.CNG);
        user.setId(1L);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setDeleted(false);
        user.setDeletedDate(null);
        user.setCopyCard(copyCard1());
        return user;
    }

    public CopyCard copyCard1(){
        CopyCard copyCard = new CopyCard();
        copyCard.setId(1L);
//        copyCard.setOwner(user1());
        copyCard.setBalance(BigDecimal.ZERO);
        copyCard.setCreatedDate(LocalDateTime.now());
        copyCard.setLastModifiedDate(LocalDateTime.now());
        copyCard.setDeleted(false);
        copyCard.setDeletedDate(null);
        return copyCard;
    }
    public User user2(){
        User user = new User();
        user.setPassword("1234");
        user.setRole(Role.USER);
        user.setUsername("username2");
        user.setEmail("a2@metu.edu.tr");
        user.setFullName("full name2");
        user.setStudentNumber("2234567");

        user.setDepartment(Department.CNG);
        user.setId(2L);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setDeleted(false);
        user.setDeletedDate(null);
        user.setCopyCard(copyCard2());
        return user;
    }


    public CopyCard copyCard2(){
        CopyCard copyCard = new CopyCard();
        copyCard.setId(2L);
//        copyCard.setOwner(user2());
        copyCard.setBalance(BigDecimal.ZERO);
        copyCard.setCreatedDate(LocalDateTime.now());
        copyCard.setLastModifiedDate(LocalDateTime.now());
        copyCard.setDeleted(false);
        copyCard.setDeletedDate(null);
        return copyCard;
    }
    public User librarian1(){
        User user = new User();
        user.setPassword("1234");
        user.setRole(Role.LIB);
        user.setUsername("username3");
        user.setEmail("a3@metu.edu.tr");
        user.setFullName("full name2");
        user.setId(2L);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setDeleted(false);
        user.setDeletedDate(null);
        return user;
    }
    public User lecturer1(){
        User user = new User();
        user.setPassword("1234");
        user.setRole(Role.LEC);
        user.setUsername("username4");
        user.setEmail("a3@metu.edu.tr");
        user.setFullName("full name2");
        user.setId(2L);
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
        roomSlot.setDay(RoomSlotDays.fromValue(LocalDate.now().getDayOfWeek().getValue()));
        roomSlot.setAvailable(true);
        roomSlot.setRoom(room1());
        roomSlot.setCreatedDate(LocalDateTime.now());
        roomSlot.setLastModifiedDate(LocalDateTime.now());
        roomSlot.setDeleted(false);
        roomSlot.setDeletedDate(null);
        return roomSlot;
    }

    public RoomReservation roomReservation1(){
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
    public RoomReservation roomReservation2(){
        RoomReservation reservation = new RoomReservation();
        reservation.setId(2L);
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

    public BookQueueRecord bookQueueRecord1(){
        BookQueueRecord bookQueueRecord = new BookQueueRecord();
        bookQueueRecord.setId(1L);
        bookQueueRecord.setCreatedDate(LocalDateTime.now());
        bookQueueRecord.setLastModifiedDate(LocalDateTime.now());
        bookQueueRecord.setDeleted(false);
        bookQueueRecord.setDeletedDate(null);
        bookQueueRecord.setBookId(book1());
        bookQueueRecord.setStatus(QueueStatus.ACTIVE);
        bookQueueRecord.setCompleteDate(null);
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        bookQueueRecord.getBookBorrowHistoryList().add(bookBorrowHistory1());
        bookQueueRecord.getBookBorrowHistoryList().add(bookBorrowHistory2());
        return bookQueueRecord;
    }
    public BookQueueRecord bookQueueRecord2(){
        BookQueueRecord bookQueueRecord = new BookQueueRecord();
        bookQueueRecord.setId(1L);
        bookQueueRecord.setCreatedDate(LocalDateTime.now());
        bookQueueRecord.setLastModifiedDate(LocalDateTime.now());
        bookQueueRecord.setDeleted(false);
        bookQueueRecord.setDeletedDate(null);
        bookQueueRecord.setBookId(book1());
        bookQueueRecord.setStatus(QueueStatus.ACTIVE);
        bookQueueRecord.setCompleteDate(null);
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        bookQueueRecord.getBookBorrowHistoryList().add(bookBorrowHistory2());
        return bookQueueRecord;
    }
    public BookBorrowHistory bookBorrowHistory1(){
        BookBorrowHistory bookBorrowHistory = new BookBorrowHistory();
        bookBorrowHistory.setId(1L);
        bookBorrowHistory.setStatus(BorrowStatus.WAITING_RETURN);
        bookBorrowHistory.setUserId(user1());
//        bookBorrowHistory.setBookQueueRecord(bookQueueRecord1());
        bookBorrowHistory.setReturnDate(null);
        bookBorrowHistory.setTakeDate(LocalDateTime.now());
        bookBorrowHistory.setCreatedDate(LocalDateTime.now());
        bookBorrowHistory.setLastModifiedDate(LocalDateTime.now());
        bookBorrowHistory.setDeleted(false);
        bookBorrowHistory.setDeletedDate(null);
        return bookBorrowHistory;
    }
    public BookBorrowHistory bookBorrowHistory2(){
        BookBorrowHistory bookBorrowHistory = new BookBorrowHistory();
        bookBorrowHistory.setId(1L);
        bookBorrowHistory.setStatus(BorrowStatus.WAITING_TAKE);
        bookBorrowHistory.setUserId(user2());
//        bookBorrowHistory.setBookQueueRecord(bookQueueRecord1());
        bookBorrowHistory.setReturnDate(null);
        bookBorrowHistory.setTakeDate(LocalDateTime.now());
        bookBorrowHistory.setCreatedDate(LocalDateTime.now());
        bookBorrowHistory.setLastModifiedDate(LocalDateTime.now());
        bookBorrowHistory.setDeleted(false);
        bookBorrowHistory.setDeletedDate(null);
        return bookBorrowHistory;
    }

    public Course course1(){
        Course course = new Course();
        course.setId(1L);
        course.setName("course name");
        course.setLecturer(lecturer1());
        course.setIsPublic(true);
        course.setImageId(image1());
        course.setCourseMaterialList(new ArrayList<>());
        course.setCourseStudentList(new ArrayList<>());
        course.getCourseMaterialList().add(courseMaterial1());
        course.getCourseStudentList().add(courseStudent1());
        course.setCreatedDate(LocalDateTime.now());
        course.setLastModifiedDate(LocalDateTime.now());
        course.setDeleted(false);
        course.setDeletedDate(null);
        return course;
    }

    public CourseStudent courseStudent1(){
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setId(1L);
        courseStudent.setStudentNumber("1234");
//        courseStudent.setCourse(course1());
        courseStudent.setStudent(user1());
        courseStudent.setCreatedDate(LocalDateTime.now());
        courseStudent.setLastModifiedDate(LocalDateTime.now());
        courseStudent.setDeleted(false);
        courseStudent.setDeletedDate(null);
        return courseStudent;
    }

    public CourseMaterial courseMaterial1(){
        CourseMaterial courseMaterial = new CourseMaterial();
        courseMaterial.setId(1L);
//        courseMaterial.setCourse(course1());
        courseMaterial.setName("material name");
        courseMaterial.setData(new byte[1]);
        courseMaterial.setFileName("file name");
        courseMaterial.setExtension("extension");
        courseMaterial.setCreatedDate(LocalDateTime.now());
        courseMaterial.setLastModifiedDate(LocalDateTime.now());
        courseMaterial.setDeleted(false);
        courseMaterial.setDeletedDate(null);
        return courseMaterial;
    }

    public ReceiptHistory receiptHistory1(){
        ReceiptHistory receiptHistory = new ReceiptHistory();
        receiptHistory.setId(1L);
        receiptHistory.setUser(user1());
        receiptHistory.setImg(image1());
        receiptHistory.setApproved(false);
        receiptHistory.setBalance(BigDecimal.ONE);
        receiptHistory.setCreatedDate(LocalDateTime.now());
        receiptHistory.setLastModifiedDate(LocalDateTime.now());
        receiptHistory.setDeleted(false);
        receiptHistory.setDeletedDate(null);
        return receiptHistory;
    }

    public BookReview bookReview1(){
        BookReview bookReview = new BookReview();
        bookReview.setId(1L);
        bookReview.setBookId(book1());
        bookReview.setUserId(user1());
        bookReview.setStar(5L);
        bookReview.setComment("comment");
        bookReview.setCreatedDate(LocalDateTime.now());
        bookReview.setLastModifiedDate(LocalDateTime.now());
        bookReview.setDeleted(false);
        bookReview.setDeletedDate(null);
        return bookReview;
    }

    public Statistics statistics1(){
        Statistics statistics = new Statistics();
        statistics.setId(1L);
        statistics.setTotalUserCount(1);
        statistics.setTotalBookCount(1);
        statistics.setAvailableBookCount(1);
        statistics.setUnavailableBookCount(1);
        statistics.setSumOfBalance(BigDecimal.ONE);
        statistics.setSumOfDebt(BigDecimal.ONE);
        statistics.setQueueCount(1);
        statistics.setDay(DayOfWeek.MONDAY);
        statistics.setCreatedDate(LocalDateTime.now());
        statistics.setLastModifiedDate(LocalDateTime.now());
        statistics.setDeleted(false);
        statistics.setDeletedDate(null);
        return statistics;
    }

    public BookQueueHoldHistoryRecord bookQueueHoldHistoryRecord1(){
        BookQueueHoldHistoryRecord bookQueueHoldHistoryRecord = new BookQueueHoldHistoryRecord();
        bookQueueHoldHistoryRecord.setId(1L);
        bookQueueHoldHistoryRecord.setUserId(1L);
        bookQueueHoldHistoryRecord.setEndDate(LocalDateTime.now().minusDays(1L));
        bookQueueHoldHistoryRecord.setBookQueueRecord(bookQueueRecord1());
        bookQueueHoldHistoryRecord.setCreatedDate(LocalDateTime.now());
        bookQueueHoldHistoryRecord.setLastModifiedDate(LocalDateTime.now());
        bookQueueHoldHistoryRecord.setDeleted(false);
        bookQueueHoldHistoryRecord.setDeletedDate(null);
        return bookQueueHoldHistoryRecord;
    }

    public BookQueueHoldHistoryRecord bookQueueHoldHistoryRecord2(){
        BookQueueHoldHistoryRecord bookQueueHoldHistoryRecord = new BookQueueHoldHistoryRecord();
        bookQueueHoldHistoryRecord.setId(2L);
        bookQueueHoldHistoryRecord.setUserId(2L);
        bookQueueHoldHistoryRecord.setEndDate(LocalDateTime.now().minusDays(1L));
        bookQueueHoldHistoryRecord.setBookQueueRecord(bookQueueRecord1());
        bookQueueHoldHistoryRecord.setCreatedDate(LocalDateTime.now());
        bookQueueHoldHistoryRecord.setLastModifiedDate(LocalDateTime.now());
        bookQueueHoldHistoryRecord.setDeleted(false);
        bookQueueHoldHistoryRecord.setDeletedDate(null);
        return bookQueueHoldHistoryRecord;
    }


    public Image imageForIntegrationTest(){
        Image image = new Image();
        image.setImageData(new byte[1]);
        image.setName("name");
        image.setType("type");
        image.setId(null);
        image.setCreatedDate(LocalDateTime.now());
        image.setLastModifiedDate(LocalDateTime.now());
        image.setDeleted(false);
        image.setDeletedDate(null);
        return image;
    }
    public Shelf shelfForIntegrationTest(){
        Shelf shelf = new Shelf();
        shelf.setFloor("1");
        shelf.setId(1L);
        shelf.setCreatedDate(LocalDateTime.now());
        shelf.setLastModifiedDate(LocalDateTime.now());
        shelf.setDeleted(false);
        shelf.setDeletedDate(null);
        return shelf;
    }

    public VerificationCode verificationCode1() {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setId(1L);
        verificationCode.setUser(user1());
        verificationCode.setCode("1234");
        verificationCode.setCreatedDate(LocalDateTime.now());
        verificationCode.setLastModifiedDate(LocalDateTime.now());
        verificationCode.setDeleted(false);
        verificationCode.setDeletedDate(null);
        return verificationCode;
    }
    public Email email1() {
        Email email = new Email();
        email.setId(1L);
        email.setSubject("subject");
        email.setContent("content");
        email.setCreatedDate(LocalDateTime.now());
        email.setLastModifiedDate(LocalDateTime.now());
        email.setDeleted(false);
        email.setDeletedDate(null);
        return email;
    }
    public Favorite favorite1() {
        Favorite favorite = new Favorite();
        favorite.setId(1L);
        favorite.setUserId(user1());
        favorite.setBookId(book1());
        favorite.setCreatedDate(LocalDateTime.now());
        favorite.setLastModifiedDate(LocalDateTime.now());
        favorite.setDeleted(false);
        favorite.setDeletedDate(null);
        return favorite;
    }
}
