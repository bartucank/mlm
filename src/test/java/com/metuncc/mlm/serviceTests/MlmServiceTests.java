package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.entity.enums.Department;
import com.metuncc.mlm.entity.enums.RoomSlotDays;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.security.JwtUserDetails;
import com.metuncc.mlm.service.MlmServices;
import com.metuncc.mlm.service.impls.MlmServicesImpl;
import com.metuncc.mlm.utils.MailUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MlmServiceTests {
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
    private RoomRepository roomRepository;

    @Mock
    private RoomSlotRepository roomSlotRepository;
    @Mock
    private BookQueueRecordRepository bookQueueRecordRepository;

    @Mock
    private BookBorrowHistoryRepository bookBorrowHistoryRepository;
    @Mock
    private MailUtil mailUtil;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseStudentRepository courseStudentRepository;
    @Mock
    private CourseMaterialRepository courseMaterialRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private BookQueueHoldHistoryRecordRepository bookQueueHoldHistoryRecordRepository;
    @Mock
    private RoomReservationRepository roomReservationRepository;
    @Mock
    private EmailRepository emailRepository;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private EbookRepository ebookRepository;
    @InjectMocks
    private MlmServicesImpl service;


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

    @Test
    public void createUser_emptyRequest(){
        UserRequest userRequest = null;
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.createUser(userRequest);

        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void createUser_already_exists_case(){
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("username");
        userRequest.setPass("123");
        userRequest.setEmail("a@metu.edu.tr");
        userRequest.setNameSurname("full name");
        userRequest.setDepartment(Department.CNG);
        userRequest.setStudentNumber("1234567");
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(userRepository.findByUsername(any())).thenReturn(dosHelper.user1());
            service.createUser(userRequest);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void createUser_valid_case() throws IOException {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("username");
        userRequest.setPass("123");
        userRequest.setEmail("a@metu.edu.tr");
        userRequest.setNameSurname("full name");
        userRequest.setDepartment(Department.CNG);
        userRequest.setStudentNumber("1234567");
        User user = dosHelper.user1();
        user.setVerified(false);
        when(userRepository.findByUsername(any())).thenReturn(null);
        when(passwordEncoder.encode(any())).thenReturn("1");
        when(userRepository.save(any())).thenReturn(dosHelper.user1());
        when(courseStudentRepository.getByStudentId(any())).thenReturn(List.of(dosHelper.courseStudent1()));
        when(courseStudentRepository.saveAll(any())).thenReturn(List.of(dosHelper.courseStudent1()));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateJwtToken(any())).thenReturn("token");
        when(userRepository.getById(any())).thenReturn(user);
        assertNotNull(service.createUser(userRequest));
    }

    @DisplayName("non metu mail")
    @Test
    public void createUser_invalidCase(){
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("username");
        userRequest.setPass("123");
        userRequest.setEmail("a@hotmail.com");
        userRequest.setNameSurname("full name");
        userRequest.setDepartment(Department.CNG);
        userRequest.setStudentNumber("1234567");
        assertThrows(MLMException.class, () -> {
            service.createUser(userRequest);
        });
    }

    @DisplayName("invalid student number")
    @Test
    public void createUser_invalidCas2(){
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("username");
        userRequest.setPass("123");
        userRequest.setEmail("a@hotmail.com");
        userRequest.setNameSurname("full name");
        userRequest.setDepartment(Department.CNG);
        userRequest.setStudentNumber("123456");
        assertThrows(MLMException.class, () -> {
            service.createUser(userRequest);
        });
    }

    @Test
    public void createShelf_nullRequest(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.createShelf(request);
        });
    }

    @Test
    public void createShelf_valid_case(){
        when( shelfRepository.save(any())).thenReturn(dosHelper.shelf1());
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        assertNotNull(service.createShelf(request));
    }

    @Test
    public void updateShelf_nullRequest(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        request.setId(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.updateShelf(request);
        });
    }

    @Test
    public void updateShelf_invalidId(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        request.setId(1L);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(shelfRepository.getById(any())).thenReturn(null);
            service.updateShelf(request);
        });
    }

    @Test
    public void updateShelf_valid_case(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        request.setId(1L);
        when(shelfRepository.getById(any())).thenReturn(new Shelf());
        service.updateShelf(request);
    }

    @Test
    public void uploadImage() throws IOException {
        ClassPathResource resource = new ClassPathResource("image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                resource.getInputStream()
        );
        Image img = new Image();
        img.setId(1L);
        when(imageRepository.save(any())).thenReturn(img);
        StatusDTO result = service.uploadImage(multipartFile);

    }

    @Test
    public void uploadImageByBase64() throws IOException {
        UploadImageByBase64 req= new UploadImageByBase64();
        req.setBase64("base64");
        when(imageRepository.save(any())).thenReturn(dosHelper.image1());
        StatusDTO result = service.uploadImageByBase64(req);
        assertNotNull(result);
        assertEquals(dosHelper.image1().getId().toString(),result.getMsg());
    }
    @Test
    public void uploadImageByBase64_invalidRequest() throws IOException {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.uploadImageByBase64(null);
        });
    }

    @Test
    public void uploadImageReturnImage() throws IOException {
        ClassPathResource resource = new ClassPathResource("image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                resource.getInputStream()
        );
        when(imageRepository.save(any())).thenReturn(dosHelper.image1());
        assertNotNull(service.uploadImageReturnImage(multipartFile));
    }

    @Test
    public void createbook_invalidShelf(){
        when(shelfRepository.getShelfById(any())).thenReturn(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.createBook(dtosHelper.getBookRequest1());
        });
    }


    @Test
    public void createbook_invalidImage(){
        when(shelfRepository.getShelfById(any())).thenReturn(dosHelper.shelf1());
        when(imageRepository.getImageById(any())).thenReturn(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.createBook(dtosHelper.getBookRequest1());
        });
    }

    @Test
    public void updateBook_invalid(){
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.updateBook(null);
        });
    }

    @Test
    public void updateBook_invalid2(){
        BookRequest bookRequest = dtosHelper.getBookRequest1();
        bookRequest.setId(1L);
        when(bookRepository.getById(any())).thenReturn(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.updateBook(bookRequest);
        });
    }
    @Test
    public void updateBook(){
        BookRequest bookRequest = dtosHelper.getBookRequest1();
        bookRequest.setId(1L);
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        assertNotNull(service.updateBook(bookRequest));
    }

    @Test
    public void deleteBook(){
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        assertNotNull(service.deleteBook(1L));
    }

    @Test
    public void deleteBook_invalid(){
        when(bookRepository.getById(any())).thenReturn(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.deleteBook(1L);
        });
    }

    @Test
    public void createRoom_invalid1(){
        CreateRoomRequest roomRequest = dtosHelper.getCreateRoomRequest1();
        roomRequest.setName(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.createRoom(roomRequest);
        });
    }

    @Test
    public void createRoom_invalid2(){
        CreateRoomRequest roomRequest = dtosHelper.getCreateRoomRequest1();
        when(imageRepository.getImageById(any())).thenReturn(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.createRoom(roomRequest);
        });
    }

    @Test
    public void createRoom(){
        CreateRoomRequest roomRequest = dtosHelper.getCreateRoomRequest1();
        when(imageRepository.getImageById(any())).thenReturn(dosHelper.image1());
        when(imageRepository.save(any())).thenReturn(dosHelper.image1());
        when(roomRepository.save(any())).thenReturn(dosHelper.room1());
        assertNotNull(service.createRoom(roomRequest));
    }

    @Test
    public void setNFCForRoom_invalid1(){
        assertThrows(MLMException.class, () -> {
            service.setNFCForRoom(null,"aaaa");
        });
    }
    @Test
    public void setNFCForRoom_invalid2(){
        when(roomRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.setNFCForRoom(1L,"aaaa");
        });
    }
    @Test
    public void setNFCForRoom(){
        when(roomRepository.getById(any())).thenReturn(dosHelper.room1());
        service.setNFCForRoom(1L,"aaaa");
    }

    @Test
    public void deleteRoom(){
        Room room = dosHelper.room1();
        room.setRoomSlotList(new ArrayList<>());
        room.getRoomSlotList().add(dosHelper.roomSlot1());
        when(roomRepository.getById(any())).thenReturn(room);
        assertNotNull(service.deleteRoom(1L));
    }

    @Test
    public void deleteRoom_invalid1(){
        when(roomRepository.getById(any())).thenReturn(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.deleteRoom(1L);
        });
    }

    @Test
    public void deleteRoom_invalid2(){
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.deleteRoom(null);
        });
    }

    @Test
    public void createSlots_invalid(){
        assertThrows(MLMException.class, () -> {
            service.createSlots(null,null,null);
        });
    }


    @Test
    public void createSlots_valid1(){
        when(roomRepository.findAll()).thenReturn(null);
        assertNotNull( service.createSlots(RoomSlotDays.FRI,"17","18"));
    }
    @Test
    public void createSlots_valid2(){
        when(roomRepository.findAll()).thenReturn(List.of(dosHelper.room1()));
        assertNotNull( service.createSlots(RoomSlotDays.FRI,"17","18"));
    }

    @Test
    public  void createShelf_invalid(){
        assertThrows(MLMException.class, () -> {
            service.createShelf(null);
        });
    }

    @Test
    public void createShelf_valid(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        when(shelfRepository.save(any())).thenReturn(dosHelper.shelf1());
        assertNotNull(service.createShelf(request));
    }

    @DisplayName("Null request")
    @Test
    public void updateShelf_invalid(){
        assertThrows(MLMException.class, () -> {
            service.updateShelf(null);
        });
    }

    @DisplayName("Shelf not found")
    @Test
    public void updateShelf_invalid2(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        request.setId(1L);
        when(shelfRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.updateShelf(request);
        });
    }

    @Test
    public void updateShelf_valid(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        request.setId(1L);
        when(shelfRepository.getById(any())).thenReturn(dosHelper.shelf1());
        assertNotNull(service.updateShelf(request));
    }

    @Test
    public void uploadImage_valid() throws IOException {
        MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
        when(imageRepository.save(any())).thenReturn(dosHelper.image1());
        assertNotNull(service.uploadImage(file));
    }

    @DisplayName("null request")
    @Test
    public void uploadImageByBase64_invalid() throws IOException {
        assertThrows(MLMException.class, () -> {
            service.uploadImageByBase64(null);
        });
    }

    @Test
    public void uploadImageByBase64_valid() throws IOException {
        UploadImageByBase64 req= new UploadImageByBase64();
        req.setBase64("base64");
        when(imageRepository.save(any())).thenReturn(dosHelper.image1());
        assertNotNull(service.uploadImageByBase64(req));
    }

    @Test
    public void uploadImageReturnImage_valid() throws IOException {
        ClassPathResource resource = new ClassPathResource("image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                resource.getInputStream()
        );
        when(imageRepository.save(any())).thenReturn(dosHelper.image1());
        assertNotNull(service.uploadImageReturnImage(multipartFile));
    }

    @Test
    public void verifyEmail_valid() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(verificationCodeRepository.getByUserAndType(any(),any())).thenReturn(dosHelper.verificationCode1());
        assertNotNull(service.verifyEmail(dosHelper.verificationCode1().getCode()));
    }

    @Test
    public void verifyEmail_invalidCode() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(verificationCodeRepository.getByUserAndType(any(),any())).thenReturn(dosHelper.verificationCode1());
        assertEquals("Wrong code.", service.verifyEmail("invalidCode").getMsg());
    }

    @Test
    public void verifyEmail_invalidUser() {
        when(userRepository.getById(any())).thenReturn(null);
        assertEquals("Unauthorized person.", service.verifyEmail("invalidCode").getMsg());
    }

    @Test
    public void createBook_valid() {
        when(shelfRepository.getShelfById(any())).thenReturn(dosHelper.shelf1());
        when(imageRepository.getImageById(any())).thenReturn(dosHelper.image1());
        when(bookRepository.save(any())).thenReturn(dosHelper.book1());
        assertNotNull(service.createBook(dtosHelper.getBookRequest1()));
    }

    @DisplayName("Null request")
    @Test
    public void createBook_invalid() {
        assertThrows(MLMException.class, () -> {
            service.createBook(null);
        });
    }

    @DisplayName("null request")
    @Test
    public void enqueue_invalidCase() {
        assertThrows(MLMException.class, () -> {
            service.enqueue(null);
        });
    }

    @DisplayName("invalid book id")
    @Test
    public void enqueue_invalidBook() {
        when(bookRepository.getById(any())).thenReturn(null);
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        assertThrows(MLMException.class, () -> {
            service.enqueue(1L);
        });
    }

    @DisplayName("bookQueueRecord null")
    @Test
    public void enqueue_invalidCase2() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.enqueue(1L);
        });
    }

    @Test
    public void enqueue_alreadyInQueue() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(dosHelper.bookQueueRecord1());
        assertThrows(MLMException.class, () -> {
            service.enqueue(1L);
        });
    }

    @Test
    public void enqueue_validCase() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        when(bookQueueRecordRepository.save(any())).thenReturn(dosHelper.bookQueueRecord1());
        assertNotNull(service.enqueue(1L));
    }

    @DisplayName("null request")
    @Test
    public void borrowBook_invalid_case() {
        assertThrows(MLMException.class, () -> {
            service.borrowBook(null, null);
        });
    }

    @DisplayName("invalid id")
    @Test
    public void borrowBook_invalid_case2() {
        when(userRepository.getById(any())).thenReturn(null);
        when(bookRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.borrowBook(1L, 1L);
        });
    }

    @Test
    public void borrowBook_valid_case() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        assertNotNull(service.borrowBook(1L, 1L));
    }

    @DisplayName("bookQueueRecord null")
    @Test
    public void borrowBook_invalid_case3() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.borrowBook(1L, 1L);
        });
    }

    @DisplayName("This user already borrowed this book")
    @Test
    public void borrowBook_invalid_case4() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(dosHelper.bookQueueRecord1());
        assertThrows(MLMException.class, () -> {
            service.borrowBook(1L, 1L);
        });
    }

    @DisplayName("Book Already taken")
    @Test
    public void borrowBook_invalid_case5() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertThrows(MLMException.class, () -> {
            service.borrowBook(1L, 1L);
        });
    }

    @DisplayName("Book reserved for someone else")
    @Test
    public void borrowBook_invalid_case6() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord2();
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertThrows(MLMException.class, () -> {
            service.borrowBook(1L, 1L);
        });
    }

    @DisplayName("Book not returned yet")
    @Test
    public void borrowBook_invalid_case7() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertThrows(MLMException.class, () -> {
            service.borrowBook(1L, 1L);
        });
    }

    @DisplayName("book reserved for user")
    @Test
    public void borrowBook_valid_case2() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user2());
        Book book = dosHelper.book1();
        book.setStatus(BookStatus.NOT_AVAILABLE);
        when(bookRepository.getById(any())).thenReturn(book);
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord2();
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        when(bookQueueHoldHistoryRecordRepository.getBookQueueHoldHistoryByBookQueue(any())).thenReturn(dosHelper.bookQueueHoldHistoryRecord2());
        assertNotNull(service.borrowBook(1L, 2L));
    }

    @DisplayName("null book id")
    @Test
    public void takeBackBook_invalidCase() {
        assertThrows(MLMException.class, () -> {
            service.takeBackBook(null);
        });
    }

    @DisplayName("book not found")
    @Test
    public void takeBackBook_invalidCase2() {
        when(bookRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.takeBackBook(1L);
        });
    }

    @DisplayName("BookQueueRecord not found")
    @Test
    public void takeBackBook_invalidCase3() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.takeBackBook(1L);
        });
    }

    @DisplayName("Book not borrowed")
    @Test
    public void takeBackBook_invalidCase4() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertThrows(MLMException.class, () -> {
            service.takeBackBook(1L);
        });
    }

    @DisplayName("Book borrowed by someone else")
    @Test
    public void takeBackBook_invalidCase5() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord2();
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertThrows(MLMException.class, () -> {
            service.takeBackBook(1L);
        });
    }

    @DisplayName("Queue continues")
    @Test
    public void takeBackBook_validCase() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        when(emailRepository.save(any())).thenReturn(dosHelper.email1());
        assertNotNull(service.takeBackBook(1L));
    }

    @DisplayName("Queue completed")
    @Test
    public void takeBackBook_validCase2() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        BookQueueRecord bookQueueRecord = dosHelper.bookQueueRecord1();
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        bookQueueRecord.getBookBorrowHistoryList().add(dosHelper.bookBorrowHistory1());
        when(bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(any(),any())).thenReturn(bookQueueRecord);
        assertNotNull(service.takeBackBook(1L));
    }

    @DisplayName("nfcCode null")
    @Test
    public void givePhysicalCopyCardToUser_invalidCase(){
        assertThrows(MLMException.class, () -> {
            service.givePhysicalCopyCardToUser(null,1L);
        });
    }
    @DisplayName("userId null")
    @Test
    public void givePhysicalCopyCardToUser_invalidCase2(){
        assertThrows(MLMException.class, () -> {
            service.givePhysicalCopyCardToUser("nfcCode",null);
        });
    }
    @DisplayName("user not found")
    @Test
    public void givePhysicalCopyCardToUser_invalidCase3(){
        when(userRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.givePhysicalCopyCardToUser("nfcCode",1L);
        });
    }

    @Test
    public void givePhysicalCopyCardToUser_validCase(){
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        assertNotNull(service.givePhysicalCopyCardToUser("nfcCode",1L));
    }

    @DisplayName("null request")
    @Test
    public void makeReservation_invalidCase(){
        assertThrows(MLMException.class, () -> {
            service.makeReservation(null);
        });
    }

    @DisplayName("RoomSlot not found")
    @Test
    public void makeReservation_invalidCase2(){
        when(roomSlotRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.makeReservation(1L);
        });
    }

    @DisplayName("RoomSlot not available")
    @Test
    public void makeReservation_invalidCase3(){
        RoomSlot roomSlot = dosHelper.roomSlot1();
        roomSlot.setAvailable(false);
        when(roomSlotRepository.getById(any())).thenReturn(roomSlot);
        assertThrows(MLMException.class, () -> {
            service.makeReservation(1L);
        });
    }

    @DisplayName("Max reservation reached")
    @Test
    public void makeReservation_invalidCase4(){
        RoomSlot roomSlot = dosHelper.roomSlot1();
        roomSlot.setAvailable(true);
        List<RoomReservation> roomReservations = new ArrayList<>();
        roomReservations.add(dosHelper.roomReservation1());
        roomReservations.add(dosHelper.roomReservation2());
        when(roomSlotRepository.getById(any())).thenReturn(roomSlot);
        when(roomReservationRepository.getRoomReservationByUserId(any(),any())).thenReturn(roomReservations);
        assertThrows(MLMException.class, () -> {
            service.makeReservation(1L);
        });
    }

    @DisplayName("valid request")
    @Test
    public void makeReservation_validCase(){
        RoomSlot roomSlot = dosHelper.roomSlot1();
        roomSlot.setAvailable(true);
        List<RoomReservation> roomReservations = new ArrayList<>();
        roomReservations.add(dosHelper.roomReservation1());
        when(roomSlotRepository.getById(any())).thenReturn(roomSlot);
        when(roomReservationRepository.getRoomReservationByUserId(any(),any())).thenReturn(roomReservations);
        when(roomReservationRepository.save(any())).thenReturn(dosHelper.roomReservation1());
        assertNotNull(service.makeReservation(1L));
    }

    @DisplayName("null request")
    @Test
    public void cancelReservation_invalidCase(){
        assertThrows(MLMException.class, () -> {
            service.cancelReservation(null);
        });
    }

    @DisplayName("RoomReservation not found")
    @Test
    public void cancelReservation_invalidCase2(){
        when(roomReservationRepository.getById(any())).thenReturn(null);
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        assertThrows(MLMException.class, () -> {
            service.cancelReservation(1L);
        });
    }

    @DisplayName("user not found")
    @Test
    public void cancelReservation_invalidCase3(){
        when(userRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.cancelReservation(1L);
        });
    }

    @DisplayName("valid request")
    @Test
    public void cancelReservation_validCase(){
        when(roomReservationRepository.getById(any())).thenReturn(dosHelper.roomReservation1());
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(roomSlotRepository.getById(any())).thenReturn(dosHelper.roomSlot1());
        assertNotNull(service.cancelReservation(1L));
    }

    @DisplayName("unauthorized user")
    @Test
    public void cancelReservation_invalidCase4(){
        when(roomReservationRepository.getById(any())).thenReturn(dosHelper.roomReservation1());
        when(userRepository.getById(any())).thenReturn(dosHelper.user2());
        assertThrows(MLMException.class, () -> {
            service.cancelReservation(1L);
        });
    }

    @DisplayName("null request")
    @Test
    public void generateQrcodeForRoom_invalidCase(){
        assertThrows(MLMException.class, () -> {
            service.generateQRcodeForRoom(null);
        });
    }

    @DisplayName("room not found")
    @Test
    public void generateQrcodeForRoom_invalidCase2(){
        when(roomRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.generateQRcodeForRoom(1L);
        });
    }

    @Test
    public void generateQrcodeForRoom_validCase(){
        when(roomRepository.getById(any())).thenReturn(dosHelper.room1());
        assertNotNull(service.generateQRcodeForRoom(1L));
    }

    @DisplayName("null request")
    @Test
    public void readingNFC_invalidCase(){
        assertThrows(MLMException.class, () -> {
            service.readingNFC(null, 1L);
        });
    }

    @DisplayName("room not found")
    @Test
    public void readingNFC_invalidCase2(){
        when(roomRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.readingNFC("nfcCode", 1L);
        });
    }

    @Test
    public void readingNFC_validCase(){
        when(roomRepository.getById(any())).thenReturn(dosHelper.room1());
        assertNotNull(service.readingNFC("nfcCode", 1L));
    }

    @DisplayName("invalid request")
    @Test
    public void approveReservation_invalidCase1(){
        assertThrows(MLMException.class, () -> {
            service.approveReservation(null, null);
        });
    }

    @DisplayName("unauthorized user")
    @Test
    public void approveReservation_invalidCase2(){
        when(authentication.getPrincipal()).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.approveReservation("nfcCode", "qrCode");
        });
    }

    @DisplayName("reservation not found")
    @Test
    public void approveReservation_invalidCase3(){
        when(roomReservationRepository.getRoomReservationByUserId(any(),any())).thenReturn(new ArrayList<>());
        assertThrows(MLMException.class, () -> {
            service.approveReservation("nfcCode", "qrCode");
        });
    }

    @DisplayName("valid case")
    @Test
    public void approveReservation_validCase(){
        RoomReservation roomReservation = dosHelper.roomReservation1();
        roomReservation.getRoomSlot().getRoom().setVerfCode("qrCode");
        roomReservation.getRoomSlot().getRoom().setNFC_no("nfcCode");
        LocalTime localTime = LocalTime.now();
        roomReservation.getRoomSlot().setStartHour(localTime.withMinute(0).withNano(0).withSecond(0));
        roomReservation.getRoomSlot().setEndHour(localTime.withMinute(59).withNano(0).withSecond(0));
        when(roomReservationRepository.getRoomReservationByUserId(any(),any())).thenReturn(List.of(roomReservation));
        assertNotNull(service.approveReservation("nfcCode", "qrCode"));
    }

    @DisplayName("invalid request")
    @Test
    public void addToFavorite_invalidCase1(){
        assertThrows(MLMException.class, () -> {
            service.addToFavorite(null);
        });
    }

    @DisplayName("book not found")
    @Test
    public void addToFavorite_invalidCase2(){
        when(bookRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.addToFavorite(1L);
        });
    }

    @DisplayName("valid case")
    @Test
    public void addToFavorite_validCase(){
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        assertNotNull(service.addToFavorite(1L));
    }

    @DisplayName("invalid request")
    @Test
    public void addEbook_invalidCase1(){
        assertThrows(MLMException.class, () -> {
            service.addEbook(null, null);
        });
    }

    @DisplayName("unsupported file")
    @Test
    public void addEbook_invalidCase2() throws IOException {
        byte[] content = "Mock file content".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "filename.txt",
                "text/plain",
                new ByteArrayInputStream(content)
        );
        assertThrows(MLMException.class, () -> {
            service.addEbook(1L, multipartFile);
        });
    }

    @DisplayName("book not found")
    @Test
    public void addEbook_invalidCase3() throws IOException {
        byte[] content = "Mock file content".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "filename.pdf",
                "application/pdf",
                new ByteArrayInputStream(content)
        );
        when(bookRepository.getById(any())).thenReturn(null);
        assertThrows(MLMException.class, () -> {
            service.addEbook(1L, multipartFile);
        });
    }

    @DisplayName("valid case")
    @Test
    public void addEbook_validCase() throws IOException {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        when(ebookRepository.findByBookId(any())).thenReturn(null);
        byte[] content = "Mock file content".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "filename.pdf",
                "application/pdf",
                new ByteArrayInputStream(content)
        );
        assertNotNull(service.addEbook(1L, multipartFile));
    }

}
