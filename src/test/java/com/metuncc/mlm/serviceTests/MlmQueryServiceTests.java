package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.request.FindUserRequest;
import com.metuncc.mlm.api.request.GetReceiptRequest;
import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.*;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.repository.specifications.BookSpecification;
import com.metuncc.mlm.repository.specifications.UserSpecification;
import com.metuncc.mlm.security.JwtUserDetails;
import com.metuncc.mlm.service.impls.MlmQueryServicesImpl;
import com.metuncc.mlm.utils.ImageUtil;
import com.metuncc.mlm.utils.excel.CourseStudentExcelWriter;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MlmQueryServiceTests {
    private DOSHelper dosHelper = new DOSHelper();
    private DTOSHelper dtosHelper = new DTOSHelper();
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ShelfRepository shelfRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseMaterialRepository courseMaterialRepository;
    @Mock
    private  Authentication authentication;
    @Mock
    private CourseStudentExcelWriter courseStudentExcelWriter;
    @Mock
    private RoomSlotRepository roomSlotRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomReservationRepository roomReservationRepository;
    @Mock
    private BookReviewRepository bookReviewRepository;
    @Mock
    private CopyCardRepository copyCardRepository;
    @Mock
    private BookQueueRecordRepository bookQueueRecordRepository;
    @Mock
    private StatisticsRepository statisticsRepository;
    @Mock
    private BookQueueHoldHistoryRecordRepository bookQueueHoldHistoryRecordRepository;
    @Mock
    private BookBorrowHistoryRepository bookBorrowHistoryRepository;
    @Mock
    private ReceiptHistoryRepository receiptHistoryRepository;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private MlmQueryServicesImpl service;

    @BeforeEach
    public void init() {
        authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(JwtUserDetails.create(dosHelper.user1()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
    public void initSecurityForLecturer() {
        SecurityContextHolder.clearContext();
        lenient().when(authentication.getPrincipal()).thenReturn(JwtUserDetails.create(dosHelper.lecturer1()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
    public void initSecurityForLibrarian() {
        SecurityContextHolder.clearContext();
        lenient().when(authentication.getPrincipal()).thenReturn(JwtUserDetails.create(dosHelper.librarian1()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @AfterEach
    public void deleteSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void getOneUserByUserName() {
//        when(userRepository.findByUsername(any())).thenReturn(dosHelper.user1());
//        service.getOneUserByUserName("asdf");
    }

    @Test
    public void getShelfById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getShelfById(null);
        });
    }

    @Test
    public void getShelfById_invalid_case2() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(shelfRepository.getById(any())).thenReturn(null);
            service.getShelfById(1L);
        });
    }

    @Test
    public void getShelfById_valid_case() {
        when(shelfRepository.getById(any())).thenReturn(dosHelper.shelf1());
        service.getShelfById(1L);
    }

    @Test
    public void getAllShelfs() {
        when(shelfRepository.findAll()).thenReturn(List.of(dosHelper.shelf1()));
        service.getAllShelfs();
    }

    @Test
    public void getImageById() throws IOException {
        ClassPathResource resource = new ClassPathResource("image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                resource.getInputStream()
        );

        Image image = new Image();
        image.setImageData(ImageUtil.compressImage(multipartFile.getBytes()));
        image.setName(multipartFile.getOriginalFilename());
        image.setType(multipartFile.getContentType());


        when(imageRepository.getImageById(any())).thenReturn(image);
        service.getImageById(1L);
    }

    @Test
    public void getImageById_null_case() throws IOException {


        when(imageRepository.getImageById(any())).thenReturn(null);
        service.getImageById(1L);
    }

    @Test
    public void getBookById_valid_case() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        service.getBookById(1L);
    }

    @Test
    public void getBookById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getBookById(null);
        });
    }

    @Test
    public void getBookbyId_invalid_case2() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(bookRepository.getById(any())).thenReturn(null);
            service.getBookById(1L);
        });
    }

    @DisplayName("getCourseMaterialbyId with valid id")
    @Test
    public void getCourseMaterialById_valid_case() throws IOException {

        ClassPathResource resource = new ClassPathResource("image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                resource.getInputStream()
        );


        CourseMaterial courseMaterial= dosHelper.courseMaterial1();
        courseMaterial.setData(ImageUtil.compressImage(multipartFile.getBytes()));
        when(courseMaterialRepository.getById(any())).thenReturn(courseMaterial);
        assertNotNull(service.getCourseMaterialById(1L));
    }

    @DisplayName("getCourseMaterialById with id null")
    @Test
    public void getCourseMaterialById_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getCourseMaterialById(null);
        });
    }

    @DisplayName("getCourseMaterialById with invalid id ")
    @Test
    public void getCourseMaterialById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(courseMaterialRepository.getById(any())).thenReturn(null);
            service.getCourseMaterialById(1L);
        });
    }

    @DisplayName("getCoursesForLecturer")
    @Test
    public void getCoursesForLecturer() {
        when(courseRepository.getCoursesByLecturerId(any())).thenReturn(List.of(dosHelper.course1()));
        assertNotNull(service.getCoursesForLecturer());
    }

    @DisplayName("getCoursesForUser")
    @Test
    public void getCoursesForUser() {
        when(courseRepository.getAllPublicCoursesAndRegisteredCourses(any())).thenReturn(List.of(dosHelper.course1()));
        assertNotNull(service.getCoursesForUser());
    }

    @DisplayName("getCourseByIdForLecturer with valid id")
    @Test
    public void getCourseByIdForLecturer_valid_case() {
        when(courseRepository.getById(any())).thenReturn(dosHelper.course1());
        assertNotNull(service.getCourseByIdForLecturer(1L));
    }

    @DisplayName("getCourseByIdForLecturer with id null")
    @Test
    public void getCourseByIdForLecturer_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getCourseByIdForLecturer(null);
        });
    }

    @DisplayName("getCourseByIdForLecturer with invalid id")
    @Test
    public void getCourseByIdForLecturer_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(courseRepository.getById(any())).thenReturn(null);
            service.getCourseByIdForLecturer(1L);
        });
    }

    @DisplayName("getCourseById with valid id")
    @Test
    public void getCourseById_valid_case() {
        when(courseRepository.getById(any())).thenReturn(dosHelper.course1());
        assertNotNull(service.getCourseById(1L));
    }

    @DisplayName("getCourseById with valid case with lecturer")
    @Test
    public void getCourseById_valid_case2() {
        initSecurityForLecturer();
        when(courseRepository.getById(any())).thenReturn(dosHelper.course1());
        assertNotNull(service.getCourseById(1L));
    }

    @DisplayName("getCourseById with id null")
    @Test
    public void getCourseById_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getCourseById(null);
        });
    }

    @DisplayName("getCourseById with invalid id")
    @Test
    public void getCourseById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(courseRepository.getById(any())).thenReturn(null);
            service.getCourseById(1L);
        });
    }

    @DisplayName("getCourseStudentExcelTemplate")
    @Test
    public void getCourseStudentExcelTemplate() {
        assertNotNull(service.getCourseStudentExcelTemplate());
    }

    @Test
    public void updateRoleOfUser_valid_case() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user2());
        assertNotNull(service.updateRoleOfUser(2L, Role.LIB));

    }

    @DisplayName("updateRoleOfUser with null id")
    @Test
    public void updateRoleOfUser_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.updateRoleOfUser(null, Role.LIB);
        });
    }

    @DisplayName("updateRoleOfUser with null role")
    @Test
    public void updateRoleOfUser_null_case2() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.updateRoleOfUser(1L, null);
        });
    }

    @DisplayName("updateRoleOfUser with invalid id")
    @Test
    public void updateRoleOfUser_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            lenient().when(userRepository.getById(any())).thenReturn(null);
            service.updateRoleOfUser(3L, Role.LIB);
        });
    }

    @DisplayName("getDeps")
    @Test
    public void getDeps() {
        assertNotNull(service.getDeps());
    }

    @DisplayName("getRoomSlotWithReservationById roomSlot available")
    @Test
    public void getRoomSlotWithReservationById_roomSlotAvailable() {
        when(roomRepository.getById(any())).thenReturn(dosHelper.room1());
        when(roomSlotRepository.getRoomSlotsByRoomId(any())).thenReturn(List.of(dosHelper.roomSlot1()));
        assertNotNull(service.getRoomSlotsWithReservationById(1L));
    }

    @DisplayName("getRoomSlotWithReservationById with invalid id")
    @Test
    public void getRoomSlotWithReservationById_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(roomRepository.getById(any())).thenReturn(null);
            service.getRoomSlotsWithReservationById(1L);
        });
    }

    @DisplayName("getRoomSlotWithReservationById roomSlot unavailable")
    @Test
    public void getRoomSlotWithReservationById_roomSlotUnavailable() {
        when(roomRepository.getById(any())).thenReturn(dosHelper.room1());
        RoomSlot roomSlot = dosHelper.roomSlot1();
        roomSlot.setAvailable(false);
        when(roomSlotRepository.getRoomSlotsByRoomId(any())).thenReturn(List.of(roomSlot));
        when(roomReservationRepository.findByRoomSlot(any())).thenReturn(dosHelper.roomReservation1());
        assertNotNull(service.getRoomSlotsWithReservationById(1L));
    }

    @DisplayName("getRoomSlotsById valid room id case")
    @Test
    public void getRoomSlotsById_valid_case() {
        when(roomRepository.getById(any())).thenReturn(dosHelper.room1());
        when(roomSlotRepository.getRoomSlotsByRoomId(any())).thenReturn(List.of(dosHelper.roomSlot1()));
        assertNotNull(service.getRoomSlotsById(1L));
    }

    @DisplayName("getRoomSlotsById with invalid room id")
    @Test
    public void getRoomSlotsById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(roomRepository.getById(any())).thenReturn(null);
            service.getRoomSlotsById(1L);
        });
    }

    @DisplayName("getExcel")
    @Test
    public void getExcel() {
        when(shelfRepository.findAll()).thenReturn(List.of(dosHelper.shelf1()));
        assertNotNull(service.getExcel());
    }

    @DisplayName("getBookReviewsBybookd id null case")
    @Test
    public void getBookReviewsByBookId_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getBookReviewsByBookId(null);
        });
    }

    @DisplayName("getBookReviewsBybookd id valid case")
    @Test
    public void getBookReviewsByBookId_valid_case() {
        Page<BookReview> bookReviewsPage = new PageImpl<>(Collections.singletonList(dosHelper.bookReview1()));
        when(bookReviewRepository.getByBookId(any(),any())).thenReturn(bookReviewsPage);
        assertNotNull(service.getBookReviewsByBookId(1L));
    }

    @DisplayName("getStatisticsForChart")
    @Test
    public void getStatisticsForChart() {
        when(userRepository.totalUserCount()).thenReturn(1);
        when(bookRepository.totalBookCount()).thenReturn(1);
        when(bookRepository.bookCountByAvailability(any())).thenReturn(1);
        when(copyCardRepository.totalBalance()).thenReturn(BigDecimal.ONE);
        when(userRepository.totalDebt()).thenReturn(BigDecimal.ONE);
        when(bookQueueRecordRepository.getBookQueueRecordByStatus(any())).thenReturn(1);
        List<Statistics> statisticsList = new ArrayList<>();
        statisticsList.add(dosHelper.statistics1());
        when(statisticsRepository.findAll()).thenReturn(statisticsList);
        assertNotNull(service.getStatisticsForChart());
    }

    @DisplayName("getQueueStatusBasedOnBookForLibrarian with invalid id")
    @Test
    public void getQueueStatusBasedOnBookForLibrarian_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(bookRepository.getById(any())).thenReturn(null);
            service.getQueueStatusBasedOnBookForLibrarian(1L);
        });
    }

    @DisplayName("getQueueStatusBasedOnBookForLibrarian invalid request")
    @Test
    public void getQueueStatusBasedOnBookForLibrarian_invalid_case2() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
            when(bookQueueRecordRepository.getBookQueueRecordByBookId(any())).thenReturn(null);
            service.getQueueStatusBasedOnBookForLibrarian(1L);
        });
    }

    @DisplayName("getQueueStatusBasedOnBookForLibrarian valid case")
    @Test
    public void getQueueStatusBasedOnBookForLibrarian_valid_case() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        when(bookQueueRecordRepository.getBookQueueRecordByBookId(any())).thenReturn(List.of(dosHelper.bookQueueRecord1()));
        assertNotNull(service.getQueueStatusBasedOnBookForLibrarian(1L));
    }
    @DisplayName("getQueueStatusBasedOnBookForLibrarian valid case active queue")
    @Test
    public void getQueueStatusBasedOnBookForLibrarian_valid_case_with_active_queue() {
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord2();
        bookQueueRecord.setStatus(QueueStatus.ACTIVE);
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        when(bookQueueRecordRepository.getBookQueueRecordByBookId(any())).thenReturn(List.of(bookQueueRecord));
        when(bookQueueHoldHistoryRecordRepository.getBookQueueHoldHistoryByBookQueue(any())).thenReturn(dosHelper.bookQueueHoldHistoryRecord1());
        assertNotNull(service.getQueueStatusBasedOnBookForLibrarian(1L));
    }

    @DisplayName("getQueueStatusBasedOnBook book id null")
    @Test
    public void getQueueStatusBasedOnBook_null_id() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(userRepository.getById(any())).thenReturn(dosHelper.user1());
            service.getQueueStatusBasedOnBook(null);
        });
    }

    @DisplayName("getQueueStatusBasedOnBook book id invalid")
    @Test
    public void getQueueStatusBasedOnBook_invalid_id() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(userRepository.getById(any())).thenReturn(dosHelper.user1());
            when(bookRepository.getById(any())).thenReturn(null);
            service.getQueueStatusBasedOnBook(1L);
        });
    }

    @DisplayName("getQueueStatusBasedOnBook book available")
    @Test
    public void getQueueStatusBasedOnBook_book_available() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        assertNotNull(service.getQueueStatusBasedOnBook(1L));
    }

    @DisplayName("getQueueStatusBasedOnBook bookQueueRecord null")
    @Test
    public void getQueueStatusBasedOnBook_bookQueueRecord_null() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(userRepository.getById(any())).thenReturn(dosHelper.user1());
            Book book = dosHelper.book1();
            book.setStatus(BookStatus.NOT_AVAILABLE);
            when(bookRepository.getById(any())).thenReturn(book);
            when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(null);
            service.getQueueStatusBasedOnBook(1L);
        });
    }

    @DisplayName("getQueueStatusBasedOnBook bookQueueRecord not null and borrow status waiting to return")
    @Test
    public void getQueueStatusBasedOnBook_bookQueueRecord_waiting_to_return() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(dosHelper.bookQueueRecord1());
        assertNotNull(service.getQueueStatusBasedOnBook(1L));
    }

    @DisplayName("getQueueStatusBasedOnBook bookQueueRecord not null and borrow status did not taken")
    @Test
    public void getQueueStatusBasedOnBook_bookQueueRecord_did_not_taken() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.getBookBorrowHistoryList().get(0).setStatus(BorrowStatus.DID_NOT_TAKEN);
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertNotNull(service.getQueueStatusBasedOnBook(1L));
    }

    @DisplayName("getQueueStatusBasedOnBook bookQueueRecord not null and borrow status returned")
    @Test
    public void getQueueStatusBasedOnBook_bookQueueRecord_returned() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.getBookBorrowHistoryList().get(0).setStatus(BorrowStatus.RETURNED);
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertNotNull(service.getQueueStatusBasedOnBook(1L));
    }

    @DisplayName("getQueueStatusBasedOnBook bookQueueRecord not null and bookBorrowHistoriesForUser empty list of user 0")
    @Test
    public void getQueueStatusBasedOnBook_bookQueueRecord_history_empty_list_0() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertNotNull(service.getQueueStatusBasedOnBook(1L));
    }

    @DisplayName("getQueueStatusBasedOnBook bookQueueRecord not null and bookBorrowHistoriesForUser empty list of user not 0")
    @Test
    public void getQueueStatusBasedOnBook_bookQueueRecord_history_empty_list_nonzero() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        List<BookBorrowHistory> bookBorrowHistoryList = new ArrayList<>();
        BookBorrowHistory bookBorrowHistory = dosHelper.bookBorrowHistory1();
        bookBorrowHistory.setStatus(BorrowStatus.WAITING_TAKE);
        bookBorrowHistoryList.add(bookBorrowHistory);
        bookQueueRecord.setBookBorrowHistoryList(bookBorrowHistoryList);
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertNotNull(service.getQueueStatusBasedOnBook(1L));
    }

    @DisplayName("getMyBooks not late")
    @Test
    public void getMyBooks_not_late() {
        BookBorrowHistory bookBorrowHistory = dosHelper.bookBorrowHistory1();
        bookBorrowHistory.setStatus(BorrowStatus.WAITING_RETURN);
        bookBorrowHistory.setBookQueueRecord(dosHelper.bookQueueRecord1());
        bookBorrowHistory.getBookQueueRecord().setBookId(dosHelper.book1());
        when(bookBorrowHistoryRepository.getByUserIdandStatus(any(),any())).thenReturn(List.of(bookBorrowHistory));
        assertNotNull(service.getMyBooks());
    }

    @DisplayName("getMyBooks late")
    @Test
    public void getMyBooks_late() {
        BookBorrowHistory bookBorrowHistory = dosHelper.bookBorrowHistory1();
        bookBorrowHistory.setStatus(BorrowStatus.WAITING_RETURN);
        bookBorrowHistory.setBookQueueRecord(dosHelper.bookQueueRecord1());
        bookBorrowHistory.getBookQueueRecord().setBookId(dosHelper.book1());
        bookBorrowHistory.setCreatedDate(LocalDateTime.now().minusDays(16));
        when(bookBorrowHistoryRepository.getByUserIdandStatus(any(),any())).thenReturn(List.of(bookBorrowHistory));
        assertNotNull(service.getMyBooks());
    }

    @DisplayName("getAllUsers")
    @Test
    public void getAllUsers() {
        when(userRepository.findAllByRoles(any())).thenReturn(List.of(dosHelper.user1()));
        assertNotNull(service.getAllUsers());
    }

    @DisplayName("getStatistics")
    @Test
    public void getStatistics() {
        when(userRepository.totalUserCount()).thenReturn(1);
        when(bookRepository.totalBookCount()).thenReturn(1);
        when(bookRepository.bookCountByAvailability(any())).thenReturn(1);
        when(copyCardRepository.totalBalance()).thenReturn(BigDecimal.ONE);
        when(userRepository.totalDebt()).thenReturn(BigDecimal.ONE);
        when(bookQueueRecordRepository.getBookQueueRecordByStatus(any())).thenReturn(1);
        assertNotNull(service.getStatistics());
    }

    @DisplayName("getReceiptHashMap")
    @Test
    public void getReceiptHashMap() {
        when(receiptHistoryRepository.findAll()).thenReturn(List.of(dosHelper.receiptHistory1()));
        assertNotNull(service.getReceiptsHashMap());
    }

    @DisplayName("getReceiptsByUser")
    @Test
    public void getReceiptsByUser() {
        when(receiptHistoryRepository.getByUserId(any())).thenReturn(List.of(dosHelper.receiptHistory1()));
        assertNotNull(service.getReceiptsByUser(1L));
    }

    @DisplayName("getReceiptsByStatus invalid request")
    @Test
    public void getReceiptsByStatus_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getReceiptsByStatus(null);
        });
    }

    @DisplayName("getReceiptsByStatus valid request")
    @Test
    public void getReceiptsByStatus_valid_case() {
        Page<ReceiptHistory> receiptPage = new PageImpl<>(List.of(dosHelper.receiptHistory1()));
        GetReceiptRequest request = new GetReceiptRequest();
        request.setPage(null);
        request.setSize(null);
        when(receiptHistoryRepository.getByStatus(any(), any())).thenReturn(receiptPage);
        assertNotNull(service.getReceiptsByStatus(request));
    }

    @DisplayName("getReceipts")
    @Test
    public void getReceipts() {
        when(receiptHistoryRepository.findAll()).thenReturn(List.of(dosHelper.receiptHistory1()));
        assertNotNull(service.getReceipts());
    }

    @DisplayName("getReceiptsOfUser")
    @Test
    public void getReceiptsOfUser(){
        when(receiptHistoryRepository.getByUserId(any())).thenReturn(List.of(dosHelper.receiptHistory1()));
        assertNotNull(service.getReceiptsOfUser());
    }

    @DisplayName("getByRef")
    @Test
    public void getByRef() throws IOException, InterruptedException {
        assertNotNull(service.getByRef("/authors/OL8811965A"));
    }

    @DisplayName("getByISBN")
    @Test
    public void getByISBN() throws IOException, InterruptedException {
        assertNotNull(service.getByISBN("9786053921936"));
    }

    @DisplayName("getBookDetailsFromExternalWithISBN")
    @Test
    public void getBookDetailsFromExternalWithISBN() throws IOException, InterruptedException {
        assertNotNull(service.getBookDetailsFromExternalWithISBN("9786053921936"));
    }

    @DisplayName("getCopyCardDetails")
    @Test
    public void getCopyCardDetails() {
        CopyCard copyCard = new CopyCard();
        copyCard.setOwner(dosHelper.user1());
        when(copyCardRepository.getByUser(any())).thenReturn(copyCard);
        assertNotNull(service.getCopyCardDetails());
    }

    @DisplayName("getRooms")
    @Test
    public void getRooms(){
        when(roomRepository.findAll()).thenReturn(List.of(dosHelper.room1()));
        assertNotNull(service.getRooms());
    }

    @DisplayName("getRoomById id null")
    @Test
    public void getRoomById_id_null(){
        assertThrows(MLMException.class, () -> {
            service.getRoomById(null);
        });
    }

    @DisplayName("getRoomById invalid id")
    @Test
    public void getRoomById_id_invalid(){
        assertThrows(MLMException.class, () -> {
            when(roomRepository.getById(any())).thenReturn(null);
            service.getRoomById(1L);
        });
    }
    @DisplayName("getRoomById")
    @Test
    public void getRoomById(){
        when(roomRepository.getById(any())).thenReturn(dosHelper.room1());
        assertNotNull(service.getRoomById(1L));
    }

    @DisplayName("getBooksBySpecification")
    @Test
    public void getBooksBySpecification(){
        FindBookRequest request = new FindBookRequest();
        request.setPage(0);
        request.setSize(7);
        Page<Book> bookPage = new PageImpl<>(List.of(dosHelper.book1()));
        when(bookRepository.findAll(any(BookSpecification.class),any(Pageable.class))).thenReturn(bookPage);
        when(bookReviewRepository.getAvgByBookId(any())).thenReturn(BigDecimal.ONE);
        assertNotNull(service.getBooksBySpecification(request));
    }

    @DisplayName("getBooksBySpecification null request")
    @Test
    public void getBooksBySpecification_null_request(){
        Page<Book> bookPage = new PageImpl<>(List.of(dosHelper.book1()));
        when(bookRepository.findAll(any(BookSpecification.class),any(Pageable.class))).thenReturn(bookPage);
        when(bookReviewRepository.getAvgByBookId(any())).thenReturn(BigDecimal.ONE);
        assertNotNull(service.getBooksBySpecification(null));
    }

    @DisplayName("getUserDetails")
    @Test
    public void getUserDetails(){
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        assertNotNull(service.getUserDetails());
    }

    @DisplayName("getUsersBySpecification null request")
    @Test
    public void getUsersBySpecification_null_request(){
        Page<User> userPage = new PageImpl<>(List.of(dosHelper.user1()));
        when(userRepository.findAll(any(UserSpecification.class),any(Pageable.class))).thenReturn(userPage);
        assertNotNull(service.getUsersBySpecifications(null));
    }

    @DisplayName("getUsersBySpecification")
    @Test
    public void getUsersBySpecification(){
        FindUserRequest request = new FindUserRequest();
        request.setPage(0);
        request.setSize(7);
        Page<User> userPage = new PageImpl<>(List.of(dosHelper.user1()));
        when(userRepository.findAll(any(UserSpecification.class),any(Pageable.class))).thenReturn(userPage);
        assertNotNull(service.getUsersBySpecifications(request));
    }
    @DisplayName("getUsersBySpecification invalid request")
    @Test
    public void getUsersBySpecification_invalid_request(){
        assertThrows(MLMException.class, () -> {
            FindUserRequest request = new FindUserRequest();
            request.setPage(null);
            request.setSize(null);
            service.getUsersBySpecifications(request);
        });
    }

    @DisplayName("getBooksByShelfId")
    @Test
    public void getBooksByShelfId(){
        when(bookRepository.getBooksByShelfId(any())).thenReturn(List.of(dosHelper.book1()));
        assertNotNull(service.getBooksByShelfId(1L));
    }

    @DisplayName("getBookById")
    @Test
    public void getBookById(){
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        when(bookReviewRepository.getByBookAndUserId(any(),any())).thenReturn(dosHelper.bookReview1());
        assertNotNull(service.getBookById(1L));
    }

    @Test
    public void testForDummyEntities(){
        Image image = dosHelper.image1();
        assertNotNull(image.toDTO());


        assertEquals(RoomSlotDays.MON,RoomSlotDays.fromValue(1));
        assertEquals(RoomSlotDays.MON,RoomSlotDays.fromValue(2));
    }

}
