package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Room;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.enums.RoomSlotDays;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.security.JwtUserDetails;
import com.metuncc.mlm.service.MlmServices;
import com.metuncc.mlm.service.impls.MlmServicesImpl;
import com.metuncc.mlm.utils.MailUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
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
    @InjectMocks
    private MlmServicesImpl service;

    @Mock
    private  Authentication authentication;

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
//        UserRequest userRequest = new UserRequest();
//        userRequest.setUsername("username");
//        userRequest.setPass("123");
//        userRequest.setEmail("a@metu.edu.tr");
//        userRequest.setNameSurname("full name");
//        MLMException thrown = assertThrows(MLMException.class, () -> {
//            when(userRepository.findByUsername(any())).thenReturn(dosHelper.user1());
//            service.createUser(userRequest);
//        });
//        assertNotNull(thrown.getMessage());
    }

    @Test
    public void createUser_valid_case(){
//        UserRequest userRequest = new UserRequest();
//        userRequest.setUsername("username");
//        userRequest.setPass("123");
//        userRequest.setNameSurname("full name");
//        userRequest.setEmail("a@metu.edu.tr");
//        when(userRepository.findByUsername(any())).thenReturn(null);
//        when(passwordEncoder.encode(any())).thenReturn("1");
//        when(userRepository.save(any())).thenReturn(dosHelper.user1());
//        assertNotNull(service.createUser(userRequest));
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
    public void verifyEmail(){
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(verificationCodeRepository.getByUserAndType(any(),any())).thenReturn(dosHelper.verificationCode1());
        assertNotNull(service.verifyEmail(dosHelper.verificationCode1().getCode()));

    }

    @Test
    public void verifyEmail_invalidCode(){
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        when(verificationCodeRepository.getByUserAndType(any(),any())).thenReturn(dosHelper.verificationCode1());
        assertEquals("Wrong code.", service.verifyEmail("invalidCode").getMsg());

    }

    @Test
    public void verifyEmail_invalidUser(){
        when(userRepository.getById(any())).thenReturn(null);

        assertEquals("Unauthorized person.", service.verifyEmail("invalidCode").getMsg());
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



}
