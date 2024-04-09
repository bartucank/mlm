package com.metuncc.mlm.integrationTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.metuncc.mlm.api.controller.AuthController;
import com.metuncc.mlm.api.controller.MLMAdminController;
import com.metuncc.mlm.api.controller.MLMController;
import com.metuncc.mlm.api.request.BookRequest;
import com.metuncc.mlm.api.request.CreateRoomRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.*;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.ExceptionHandler;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.utils.MailUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration Tests")
public class IntegrationTests {

    private final ObjectMapper objectMapper = new ObjectMapper();


    private DOSHelper dosHelper = new DOSHelper();
    private DTOSHelper dtosHelper = new DTOSHelper();

    @MockBean
    private MailUtil mailUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ShelfRepository shelfRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BookQueueHoldHistoryRecordRepository bookQueueHoldHistoryRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BookQueueRecordRepository bookQueueRecordRepository;

    @Autowired
    private BookBorrowHistoryRepository bookBorrowHistoryRepository;
    @Autowired
    private RoomSlotRepository roomSlotRepository;
    @Autowired
    private RoomReservationRepository roomReservationRepository;


    @BeforeAll
    public void before() {
        doNothing().when(mailUtil).sendCustomEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString());
        //Shelf;
        Shelf shelf = shelfRepository.save(dosHelper.shelfForIntegrationTest());
        //Image
        Image image = imageRepository.save(dosHelper.imageForIntegrationTest());
        //User 1;
        User user = dosHelper.user1();
        user.setId(null);
        user.getCopyCard().setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(true);
        userRepository.save(user);
        //User 2;
        User user2 = dosHelper.user2();
        user2.setId(null);
        user2.getCopyCard().setId(null);
        user2.setVerified(true);
        user2.setPassword(passwordEncoder.encode(user2.getPassword()));
        userRepository.save(user2);
        //User 3;
        User lecturer = dosHelper.lecturer1();
        lecturer.setVerified(true);
        lecturer.setId(null);
        lecturer.setPassword(passwordEncoder.encode(lecturer.getPassword()));
        userRepository.save(lecturer);

        //Room 1;
        Room room = dosHelper.room1();
        room.setId(null);
        room.setQrImage(image);
        room.setImageId(image);
        room.setRoomSlotList(new ArrayList<>());

        RoomSlotDays tomorrowEnum = RoomSlotDays.fromValue(LocalDateTime.now().plusDays(1L).getDayOfWeek().getValue());
        for (int i = Integer.valueOf("08"); i <= Integer.valueOf("23"); i++) {
            LocalTime localTimeStart = LocalTime.of(i, 0, 0, 0);
            LocalTime localTimeEnd = LocalTime.of(i, 59, 0, 0);
            RoomSlot roomSlot = new RoomSlot();
            roomSlot.setStartHour(localTimeStart);
            roomSlot.setEndHour(localTimeEnd);
            roomSlot.setDay(tomorrowEnum);
            roomSlot.setRoom(room);
            roomSlot.setAvailable(true);
            room.getRoomSlotList().add(roomSlot);
        }
        room = roomRepository.save(room);
        //Book 1;
        Book book = dosHelper.book1();
        book.setShelfId(shelf);
        book.setImageId(image);
        book.setId(null);
        bookRepository.save(book);


    }
    public <T> String mapToJson(T obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    public Object getFromJson(String content){
        objectMapper.findAndRegisterModules();
        objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Object response = null;
        try {
            response = objectMapper.readValue(content, Object.class);
        } catch (JsonProcessingException e) {
            assertFalse(true);
        }

        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) response;
        Object data = linkedHashMap.get("data");
        return data;

    }

    public String loginWithUser(User user) throws Exception {
        //First login;
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(user.getUsername());
        userRequest.setPass(user.getPassword());
        String userJson = mapToJson(userRequest);
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();
        assertEquals(200, loginResult.getResponse().getStatus());
        String loginContent = loginResult.getResponse().getContentAsString();
        LinkedHashMap loginResponse = (LinkedHashMap) getFromJson(loginContent);
        String token = (String) loginResponse.get("jwt");
        return token;
    }
    @DisplayName("Trying to register with metu mail.")
    @Test
    @Order(1)
    public void testCreateUser() throws Exception {
        String userJson = mapToJson(dtosHelper.userRequest1());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals(true,response.get("needVerify"));
        assertNotNull(response.get("jwt"));
    }
    @DisplayName("Trying to register with mail that is not metu mail.")
    @Test
    @Order(2)
    public void testCreateUserInvalid() throws Exception {
        UserRequest userRequest = dtosHelper.userRequest1();
        userRequest.setUsername("notmetuian");
        userRequest.setEmail("notmetuian@gmail.com");
        userRequest.setStudentNumber("1");
        String userJson = mapToJson(userRequest);
        assertThatThrownBy(() -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson));
        }).isExactlyInstanceOf(NestedServletException.class)
                .hasCauseExactlyInstanceOf(MLMException.class)
                .hasMessageContaining("Only metuians can register this application.");

    }


    @DisplayName("Creating book with valid informations.")
    @Test
    @Order(3)
    public void testCreateBook() throws Exception {
        Long count = bookRepository.count();
        BookRequest bookRequest = dtosHelper.getBookRequest1();
        bookRequest.setShelfId(shelfRepository.findAll().get(0).getId());
        bookRequest.setImageId(imageRepository.findAll().get(0).getId());
        String userJson = mapToJson(bookRequest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
        assertNotEquals(count,bookRepository.count());
    }

    @DisplayName("Creating book with invalid informations.")
    @Test
    @Order(4)
    public void testCreateBookInvalid() throws Exception {
        Long count = bookRepository.count();
        BookRequest bookRequest = dtosHelper.getBookRequest1();
        bookRequest.setName(null);
        bookRequest.setIsbn(null);
        bookRequest.setShelfId(null);
        bookRequest.setImageId(null);
        String userJson = mapToJson(bookRequest);

        assertThatThrownBy(() -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson));
        }).isExactlyInstanceOf(NestedServletException.class)
                .hasCauseExactlyInstanceOf(MLMException.class);

        assertEquals(count,bookRepository.count());
    }



    @DisplayName("Trying to borrow a book to a user.")
    @Test
    @Order(5)
    public void testBorrowBook() throws Exception {
        Book before = bookRepository.findAll().get(0);
        assertEquals(BookStatus.AVAILABLE,before.getStatus());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookId",before.getId().toString())
                        .param("userId",userRepository.findAll().get(0).getId().toString())
                )

                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
        Book after   = bookRepository.getById(before.getId());
        assertEquals(BookStatus.NOT_AVAILABLE,after.getStatus());
    }
    @DisplayName("Trying to borrow a book that not available and user is not in the queue")
    @Test
    @Order(6)
    public void testBorrowBookInvalid() throws Exception {
        User userForBorrow = userRepository.findAll().get(0);
        User userForError = userRepository.findAll().get(1);
        Book before = bookRepository.findAll().get(0);
        assertEquals(BookStatus.AVAILABLE,before.getStatus());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .param("bookId",before.getId().toString())
                .param("userId",userForBorrow.getId().toString())
        ).andReturn();
        before = bookRepository.getByStatus(BookStatus.NOT_AVAILABLE).get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,before.getStatus());

        Book finalBefore = before;
        assertThatThrownBy(() -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/borrow")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("bookId", finalBefore.getId().toString())
                    .param("userId",userForError.getId().toString()));
        }).isExactlyInstanceOf(NestedServletException.class)
                .hasCauseExactlyInstanceOf(MLMException.class);
        BookQueueRecord bookQueueRecord = bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(before, QueueStatus.ACTIVE);
        assertEquals(1,bookQueueRecord.getBookBorrowHistoryList().size());
        assertEquals(userForBorrow.getId(),bookQueueRecord.getBookBorrowHistoryList().get(0).getUserId().getId());
    }
    @DisplayName("Enter queue for borrowed book.")
    @Test
    @Order(7)
    public void testEnqueue() throws Exception {

        User userForBorrow = userRepository.findAll().get(0);
        User userForEnqueue = userRepository.findAll().get(1);
        Book before = bookRepository.findAll().get(0);
        assertEquals(BookStatus.AVAILABLE,before.getStatus());
        MvcResult borrow = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .param("bookId",before.getId().toString())
                .param("userId",userForBorrow.getId().toString())
        ).andReturn();
        before = bookRepository.getByStatus(BookStatus.NOT_AVAILABLE).get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,before.getStatus());


        String token = loginWithUser(dosHelper.user2());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/enqueue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id",bookRepository.findAll().get(0).getId().toString())
                .header("Authorization", "Bearer " + token)
                )
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
        before = bookRepository.findAll().get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,before.getStatus());
        BookQueueRecord bookQueueRecord = bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(before, QueueStatus.ACTIVE);
        assertEquals(2,bookQueueRecord.getBookBorrowHistoryList().size());
        assertEquals(userForEnqueue.getId(),bookQueueRecord.getBookBorrowHistoryList().get(1).getUserId().getId());

    }
    @DisplayName("Trying to take back a book from the user.")
    @Test
    @Order(8)
    public void testTakeBackBook() throws Exception {
        User userForBorrow = userRepository.findAll().get(0);
        Book before = bookRepository.findAll().get(0);
        assertEquals(BookStatus.AVAILABLE,before.getStatus());
        MvcResult borrow = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .param("bookId",before.getId().toString())
                .param("userId",userForBorrow.getId().toString())
        ).andReturn();
        before = bookRepository.getByStatus(BookStatus.NOT_AVAILABLE).get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,before.getStatus());



        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/takeBackBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookId",before.getId().toString())
                        .param("userId",userForBorrow.getId().toString())
                )

                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
        before = bookRepository.getById(before.getId());
        assertEquals(BookStatus.AVAILABLE,before.getStatus());
        assertNotNull(bookQueueHoldHistoryRecordRepository.findAll());
    }

    @DisplayName("Trying to make a reservation for a room slot.")
    @Test
    @Order(9)
    public void tryingToReservation() throws Exception {
        Room room = roomRepository.findAll().get(0);
        assertEquals(0,roomReservationRepository.count());
        assertNotEquals(0,room.getRoomSlotList().size());
        User user = userRepository.findAll().get(0);
        String token = loginWithUser(dosHelper.user1());
        RoomSlot roomSlot = room.getRoomSlotList().get(0);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/makeReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("roomSlotId",roomSlot.getId().toString())
                        .header("Authorization", "Bearer " + token)
                )
                .andReturn();
        room = roomRepository.getById(room.getId());
        for (RoomSlot slot : room.getRoomSlotList()) {
            if(slot.getId().equals(roomSlot.getId())){
                roomSlot = slot;
                break;
            }
        }
        assertEquals(false,roomSlot.getAvailable());
        assertEquals(1,roomReservationRepository.count());
        assertEquals(user.getId(),roomReservationRepository.findAll().get(0).getUserId());
    }
    @DisplayName("Trying to make a reservation for a room slot but s/he has already two reservation.")
    @Test
    @Order(10)
    public void tryingToReservationInvalid() throws Exception {
        Room room = roomRepository.findAll().get(0);
        User user = userRepository.findAll().get(0);
        String token = loginWithUser(dosHelper.user1());
        List<RoomSlot> slots = roomSlotRepository.getRoomSlotsByRoomId(room.getId());
        RoomSlot roomSlot = slots.get(0);
        RoomSlot roomSlot1 = slots.get(1);
        RoomSlot roomSlot2 = slots.get(2);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/makeReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("roomSlotId",roomSlot.getId().toString())
                        .header("Authorization", "Bearer " + token)
                )
                .andReturn();
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/makeReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("roomSlotId",roomSlot1.getId().toString())
                        .header("Authorization", "Bearer " + token)
                )
                .andReturn();

        RoomSlot finalRoomSlot = roomSlot2;
        assertThatThrownBy(() -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/user/makeReservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("roomSlotId", finalRoomSlot.getId().toString())
                    .header("Authorization", "Bearer " + token)
            );
        }).isExactlyInstanceOf(NestedServletException.class)
                .hasCauseExactlyInstanceOf(MLMException.class);
        assertEquals(2,roomReservationRepository.count());
        roomSlot2 = roomSlotRepository.getById(roomSlot2.getId());
        assertEquals(true,roomSlot2.getAvailable());



    }



}
