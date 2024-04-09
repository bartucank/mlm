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
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.entity.enums.Department;
import com.metuncc.mlm.entity.enums.VerificationType;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @BeforeAll
    public void before() {
        Mockito.doNothing().when(mailUtil).sendCustomEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString());
        shelfRepository.save(dosHelper.shelf1());
        Image image = dosHelper.image1();
        image.setId(null);
        image = imageRepository.save(image);
        User user = dosHelper.user1();
        user.setId(null);
        user.getCopyCard().setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(true);
        userRepository.save(user);
        User user2 = dosHelper.user2();
        user2.setId(null);
        user2.getCopyCard().setId(null);
        user2.setVerified(true);
        user2.setPassword(passwordEncoder.encode(user2.getPassword()));
        userRepository.save(user2);
        User lecturer = dosHelper.lecturer1();
        lecturer.setVerified(true);
        lecturer.setId(null);
        lecturer.setPassword(passwordEncoder.encode(lecturer.getPassword()));
        userRepository.save(lecturer);

        Room room = dosHelper.room1();
        room.setId(null);
        room.setQrImage(image);
        room.setImageId(image);
        roomRepository.save(room);

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
    }

    @DisplayName("Update book with valid informations.")
    @Test
    @Order(4)
    public void testUpdateBook() throws Exception {
        BookRequest bookRequest = dtosHelper.getBookRequest1();
        bookRequest.setId(bookRepository.findAll().get(0).getId());
        bookRequest.setShelfId(shelfRepository.findAll().get(0).getId());
        bookRequest.setImageId(imageRepository.findAll().get(0).getId());
        String userJson = mapToJson(bookRequest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/book/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
    }


    @DisplayName("Borrow an available book to a user.")
    @Test
    @Order(5)
    public void testBorrowBook() throws Exception {
        Book book = bookRepository.findAll().get(0);
        assertEquals(BookStatus.AVAILABLE,book.getStatus());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookId",bookRepository.findAll().get(0).getId().toString())
                        .param("userId",userRepository.findAll().get(0).getId().toString())
                )

                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
        book = bookRepository.findAll().get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,book.getStatus());
    }
    @DisplayName("Enter queue for borrowed book.")
    @Test
    @Order(6)
    public void testEnqueue() throws Exception {
        Book book = bookRepository.findAll().get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,book.getStatus());
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
        book = bookRepository.findAll().get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,book.getStatus());
    }
    @DisplayName("Take back a book from a user.")
    @Test
    @Order(7)
    public void testTakeBackBook() throws Exception {
        assertEquals(new ArrayList<>(), bookQueueHoldHistoryRecordRepository.findAll());
        Book book = bookRepository.findAll().get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,book.getStatus());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/book/takeBackBook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookId",book.getId().toString())
                        .param("userId",userRepository.findAll().get(0).getId().toString())
                )

                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
        book = bookRepository.findAll().get(0);
        assertEquals(BookStatus.NOT_AVAILABLE,book.getStatus());
        assertNotNull(bookQueueHoldHistoryRecordRepository.findAll());
    }

    @DisplayName("Create room with valid informations.")
    @Test
    @Order(8)
    public void testCreateRoom() throws Exception {
        CreateRoomRequest request = dtosHelper.getCreateRoomRequest1();
        request.setImageId(imageRepository.findAll().get(0).getId());
        String json = mapToJson(request);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
    }
    @DisplayName("Delete a room")
    @Test
    @Order(9)
    public void testDeleteRoom() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/deleteRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("roomId",roomRepository.findAll().get(0).getId().toString())
                )

                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        LinkedHashMap response = (LinkedHashMap) getFromJson(content);
        assertEquals("S",response.get("statusCode"));
    }



}
