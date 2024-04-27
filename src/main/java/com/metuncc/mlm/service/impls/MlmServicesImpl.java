package com.metuncc.mlm.service.impls;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.dto.OpenLibraryBookAuthor;
import com.metuncc.mlm.dto.OpenLibraryBookAuthorDetail;
import com.metuncc.mlm.dto.OpenLibraryBookDetails;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.dto.google.GoogleResponse;
import com.metuncc.mlm.dto.google.Item;
import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.*;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.security.JwtUserDetails;
import com.metuncc.mlm.service.MlmServices;
import com.metuncc.mlm.utils.ImageUtil;
import com.metuncc.mlm.utils.MailUtil;
import com.metuncc.mlm.utils.excel.ExcelBookRow;
import com.metuncc.mlm.utils.excel.ExcelReader;
import net.glxn.qrgen.QRCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MlmServicesImpl implements MlmServices {
    private BookReviewRepository bookReviewRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ShelfRepository shelfRepository;
    private RoomRepository roomRepository;
    private ImageRepository imageRepository;
    private BookRepository bookRepository;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private MailUtil mailUtil;
    private VerificationCodeRepository verificationCodeRepository;
    private BookBorrowHistoryRepository bookBorrowHistoryRepository;
    private BookQueueRecordRepository bookQueueRecordRepository;
    private CopyCardRepository copyCardRepository;
    private RoomSlotRepository roomSlotRepository;
    private RoomReservationRepository roomReservationRepository;
    private BookQueueHoldHistoryRecordRepository bookQueueHoldHistoryRecordRepository;
    private ReceiptHistoryRepository receiptHistoryRepository;
    private EmailRepository emailRepository;
    private CourseRepository courseRepository;
    private CourseMaterialRepository courseMaterialRepository;
    private CourseStudentRepository courseStudentRepository;
    private FavoriteRepository favoriteRepository;
    private EbookRepository ebookRepository;
    private final StatusDTO success = StatusDTO.builder().statusCode("S").msg("Success!").build();
    private final StatusDTO error = StatusDTO.builder().statusCode("E").msg("Error!").build();

    @Value("${webpage.link:https://metu.edu.tr}")
    private String webpageLink;

    public MlmServicesImpl(BookReviewRepository bookReviewRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ShelfRepository shelfRepository, RoomRepository roomRepository, ImageRepository imageRepository, BookRepository bookRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, MailUtil mailUtil, VerificationCodeRepository verificationCodeRepository, BookBorrowHistoryRepository bookBorrowHistoryRepository, BookQueueRecordRepository bookQueueRecordRepository, CopyCardRepository copyCardRepository, RoomSlotRepository roomSlotRepository, RoomReservationRepository roomReservationRepository, BookQueueHoldHistoryRecordRepository bookQueueHoldHistoryRecordRepository, ReceiptHistoryRepository receiptHistoryRepository, EmailRepository emailRepository, CourseRepository courseRepository, CourseMaterialRepository courseMaterialRepository, CourseStudentRepository courseStudentRepository, FavoriteRepository favoriteRepository, EbookRepository ebookRepository) {
        this.bookReviewRepository = bookReviewRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.shelfRepository = shelfRepository;
        this.roomRepository = roomRepository;
        this.imageRepository = imageRepository;
        this.bookRepository = bookRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailUtil = mailUtil;
        this.verificationCodeRepository = verificationCodeRepository;
        this.bookBorrowHistoryRepository = bookBorrowHistoryRepository;
        this.bookQueueRecordRepository = bookQueueRecordRepository;
        this.copyCardRepository = copyCardRepository;
        this.roomSlotRepository = roomSlotRepository;
        this.roomReservationRepository = roomReservationRepository;
        this.bookQueueHoldHistoryRecordRepository = bookQueueHoldHistoryRecordRepository;
        this.receiptHistoryRepository = receiptHistoryRepository;
        this.emailRepository = emailRepository;
        this.courseRepository = courseRepository;
        this.courseMaterialRepository = courseMaterialRepository;
        this.courseStudentRepository = courseStudentRepository;
        this.favoriteRepository = favoriteRepository;
        this.ebookRepository = ebookRepository;
    }

    @Override
    public LoginResponse createUser(UserRequest userRequest) {
        if (Objects.isNull(userRequest) ||
                Objects.isNull(userRequest.getUsername()) ||
                StringUtils.isEmpty(userRequest.getUsername()) ||
                Objects.isNull(userRequest.getPass()) ||
                StringUtils.isEmpty(userRequest.getPass()) ||
                Objects.isNull(userRequest.getNameSurname()) ||
                StringUtils.isEmpty(userRequest.getNameSurname()) ||
                Objects.isNull(userRequest.getEmail()) ||
                StringUtils.isEmpty(userRequest.getEmail()) ||
                Objects.isNull(userRequest.getDepartment()) ||
                Objects.isNull(userRequest.getStudentNumber())
        ) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if (!userRequest.getEmail().endsWith("@metu.edu.tr")) {
            throw new MLMException(ExceptionCode.ONLY_METU);
        }
        //check student number pattern. it should be in 7 digits.
        if (!userRequest.getStudentNumber().matches("[0-9]{7}")) {
            throw new MLMException(ExceptionCode.INVALID_STUDENT_NUMBER);
        }
        if (Objects.nonNull(userRepository.findByUsername(userRequest.getUsername())) ||
                Objects.nonNull(userRepository.findByEmail(userRequest.getEmail()))) {
            throw new MLMException(ExceptionCode.USERNAME_ALREADY_TAKEN);
        }
        User user = new User().fromRequest(userRequest);
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(userRequest.getPass()));
        user = userRepository.save(user);


        //Check inv exists for any course;
        List<CourseStudent> courseStudentList = courseStudentRepository.getByStudentId(user.getStudentNumber());
        for (CourseStudent courseStudent : courseStudentList) {
            courseStudent.setStudent(user);
        }
        courseStudentRepository.saveAll(courseStudentList);
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCode(generateRandomCode(4));
        verificationCode.setVerificationType(VerificationType.REGISTER);
        verificationCodeRepository.save(verificationCode);
        mailUtil.sendVerifyEmailEmail(user.getEmail(), verificationCode.getCode());
        CopyCard copyCard = new CopyCard();
        copyCard.setBalance(BigDecimal.ZERO);
        copyCard.setNfcCode("");
        copyCard.setOwner(user);
        user.setCopyCard(copyCard);
        userRepository.save(user);
        return login(userRequest);
    }

    private static String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomDigit = random.nextInt(10);
            code.append(randomDigit);
        }
        return code.toString();
    }

    @Override
    public LoginResponse login(UserRequest userRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userRequest.getUsername(),
                        userRequest.getPass());
        Authentication authentication = authenticationManager
                .authenticate(usernamePasswordAuthenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateJwtToken(authentication);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setJwt(token);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        loginResponse.setNeedVerify(!user.getVerified());
        return loginResponse;
    }

    @Override
    public StatusDTO createShelf(ShelfCreateRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getFloor())) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Shelf shelf = new Shelf().fromRequest(request);
        shelf = shelfRepository.save(shelf);
        return StatusDTO.builder().statusCode("S").msg(shelf.getId().toString()).build();
    }

    @Override
    public StatusDTO updateShelf(ShelfCreateRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getId()) || Objects.isNull(request.getFloor())) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Shelf shelf = shelfRepository.getById(request.getId());
        if (Objects.isNull(shelf)) {
            throw new MLMException(ExceptionCode.SHELF_NOT_FOUND);
        }
        shelf = shelf.fromRequest(request);
        shelfRepository.save(shelf);
        return success;
    }

    @Override
    public StatusDTO uploadImage(MultipartFile file) throws IOException {

        Image img = new Image();
        img.setImageData(ImageUtil.compressImage(file.getBytes()));
        img.setName(file.getOriginalFilename());
        img.setType(file.getContentType());

        img = imageRepository.save(img);
        return StatusDTO.builder().statusCode("S").msg(img.getId().toString()).build();
    }
    @Override
    public StatusDTO uploadImageByBase64(UploadImageByBase64 request) throws IOException {
        if(Objects.isNull(request)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        byte[] byteArray = Base64.getDecoder().decode(request.getBase64());

        Image img = new Image();
        img.setImageData(ImageUtil.compressImage(byteArray));
        img.setName("frombase4");
        img.setType("jpg");
        img = imageRepository.save(img);
        return StatusDTO.builder().statusCode("S").msg(img.getId().toString()).build();
    }

    @Override
    public Image uploadImageReturnImage(MultipartFile file) throws IOException {

        Image img = new Image();
        img.setImageData(ImageUtil.compressImage(file.getBytes()));
        img.setName(file.getOriginalFilename());
        img.setType(file.getContentType());

        img = imageRepository.save(img);
        return img;
    }

    @Override
    public StatusDTO verifyEmail(String code) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        if (Objects.isNull(user)) {
            return StatusDTO.builder().statusCode("E").msg("Unauthorized person.").build();
        }
        VerificationCode verificationCode = verificationCodeRepository.getByUserAndType(user, VerificationType.REGISTER);
        if (verificationCode.getCode().equals(code)) {
            verificationCode.setIsCompleted(true);
            verificationCodeRepository.save(verificationCode);
            user.setVerified(true);
            userRepository.save(user);
            return success;
        } else {
            return StatusDTO.builder().statusCode("E").msg("Wrong code.").build();
        }
    }

    @Override
    public StatusDTO createBook(BookRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getName()) || Objects.isNull(request.getIsbn())
                || Objects.isNull(request.getAuthor()) || Objects.isNull(request.getCategory())
                || Objects.isNull(request.getShelfId()) || Objects.isNull(request.getImageId())) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Shelf shelf = shelfRepository.getShelfById(request.getShelfId());
        if (Objects.isNull(shelf)) {
            throw new MLMException(ExceptionCode.SHELF_NOT_FOUND);
        }
        Image image = imageRepository.getImageById(request.getImageId());
        if (Objects.isNull(image)) {
            throw new MLMException(ExceptionCode.IMAGE_NOT_FOUND);
        }
        Book book = new Book().fromRequest(request);
        book.setStatus(BookStatus.AVAILABLE);
        book.setShelfId(shelf);
        book.setImageId(image);
        bookRepository.save(book);
        return success;
    }

    @Override
    public StatusDTO updateBook(BookRequest request) {
        if (Objects.isNull(request)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Book book = bookRepository.getById(request.getId());
        if (Objects.isNull(book)) {
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        book = book.fromRequestUpdate(request);
        bookRepository.save(book);
        return success;
    }

    @Override
    public StatusDTO deleteBook(Long id) {
        Book book = bookRepository.getById(id);
        if (Objects.isNull(book)) {
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        book.setDeleted(true);
        book.setDeletedDate(LocalDateTime.now());
        bookRepository.save(book);
        return success;
    }

    @Override
    public StatusDTO createRoom(CreateRoomRequest request) {
        if (
                Objects.isNull(request) ||
                        Objects.isNull(request.getImageId()) ||
                        Objects.isNull(request.getName())
        ) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Image image = imageRepository.getImageById(request.getImageId());
        if (Objects.isNull(image)) {
            throw new MLMException(ExceptionCode.IMAGE_NOT_FOUND);
        }
        Room room = new Room();
        room.setName(request.getName());
        room.setImageId(image);
        room.setRoomSlotList(new ArrayList<>());
        room = roomRepository.save(room);
        try {
            String code = getUniqueVerfCodeForRoom();
            ByteArrayOutputStream stream = QRCode
                    .from(code)
                    .withSize(250, 250)
                    .stream();
            ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());
            byte[] qrCodeImageData = stream.toByteArray();
            Image img = new Image();
            img.setImageData(ImageUtil.compressImage(qrCodeImageData));
            img.setName("QR");
            img.setType("png");
            img = imageRepository.save(img);
            room.setVerfCode(code);
            room.setQrImage(img);
        } catch (Exception e) {
            //do nothing for now.
        }
        room.setRoomSlotList(new ArrayList<>());
        RoomSlotDays todayEnum = RoomSlotDays.fromValue(LocalDateTime.now().getDayOfWeek().getValue());
        RoomSlotDays tomorrowEnum = RoomSlotDays.fromValue(LocalDateTime.now().plusDays(1L).getDayOfWeek().getValue());
        RoomSlotDays twoDatsLaterEnum = RoomSlotDays.fromValue(LocalDateTime.now().plusDays(2L).getDayOfWeek().getValue());
        List<RoomSlotDays> days = new ArrayList<>();
        days.add(todayEnum);
        days.add(tomorrowEnum);
        days.add(twoDatsLaterEnum);
        LocalTime time = LocalTime.now();
        for (RoomSlotDays day : days) {

            for (int i = Integer.valueOf("08"); i <= Integer.valueOf("23"); i++) {
                LocalTime localTimeStart = LocalTime.of(i, 0, 0, 0);
                LocalTime localTimeEnd = LocalTime.of(i, 59, 0, 0);
                RoomSlot roomSlot = new RoomSlot();
                roomSlot.setStartHour(localTimeStart);
                roomSlot.setEndHour(localTimeEnd);
                roomSlot.setRoom(room);
                roomSlot.setDay(day);
                roomSlot.setAvailable(day.equals(todayEnum)?!(i <= time.getHour()):true);
                room.getRoomSlotList().add(roomSlot);
            }

        }
        roomRepository.save(room);
        return success;
    }

    @Override
    public StatusDTO setNFCForRoom(Long roomId, String nfcNo) {
        if (Objects.isNull(roomId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Room room = roomRepository.getById(roomId);
        if (Objects.isNull(room)) {
            throw new MLMException(ExceptionCode.ROOM_NOT_FOUND);
        }
        room.setNFC_no(nfcNo);
        roomRepository.save(room);
        return success;
    }

    @Override
    public StatusDTO deleteRoom(Long id) {
        if (Objects.isNull(id)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Room room = roomRepository.getById(id);
        if (Objects.isNull(room)) {
            throw new MLMException(ExceptionCode.ROOM_NOT_FOUND);
        }
        room.setDeleted(true);
        room.setDeletedDate(LocalDateTime.now());
        roomSlotRepository.deleteAll(room.getRoomSlotList());
        roomRepository.save(room);
        return success;
    }

    @Override
    public StatusDTO createSlots(RoomSlotDays day, String start, String end) {
        if (Objects.isNull(day)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        List<Room> roomList = roomRepository.findAll();
        if (Objects.isNull(roomList)) {
            return success;
        }
        for (Room room : roomList) {
            if(Objects.isNull(room.getRoomSlotList())){
                room.setRoomSlotList(new ArrayList<>());
            }
            for (int i = Integer.valueOf(start); i <= Integer.valueOf(end); i++) {
                LocalTime localTimeStart = LocalTime.of(i, 0, 0, 0);
                LocalTime localTimeEnd = LocalTime.of(i, 59, 0, 0);
                RoomSlot roomSlot = new RoomSlot();
                roomSlot.setStartHour(localTimeStart);
                roomSlot.setEndHour(localTimeEnd);
                roomSlot.setDay(day);
                roomSlot.setRoom(room);
                roomSlot.setAvailable(true);
                room.getRoomSlotList().add(roomSlot);
            }
        }
        roomRepository.saveAll(roomList);
        return success;
    }

    @Override
    public StatusDTO enqueue(Long bookId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        if (Objects.isNull(bookId) || Objects.isNull(jwtUser.getId())) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        User user = userRepository.getById(jwtUser.getId());
        Book book = bookRepository.getById(bookId);
        if (Objects.isNull(book) || Objects.isNull(user)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        BookQueueRecord bookQueueRecord = bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(book, QueueStatus.ACTIVE);
        if (Objects.isNull(bookQueueRecord)) {
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        BookBorrowHistory bookBorrowHistory = null;
        if (!CollectionUtils.isEmpty(bookQueueRecord.getBookBorrowHistoryList())) {
            bookBorrowHistory = bookQueueRecord.getBookBorrowHistoryList()
                    .stream()
                    .filter(c -> c.getUserId() != null && c.getUserId().getId().equals(jwtUser.getId()))
                    .findFirst()
                    .orElse(null);
        }
        //already in queue;
        if (Objects.nonNull(bookBorrowHistory)) {
            throw new MLMException(ExceptionCode.ALREADY_IN_QUEUE);
        }
        bookBorrowHistory = new BookBorrowHistory();
        bookBorrowHistory.setUserId(user);
        bookBorrowHistory.setStatus(BorrowStatus.WAITING_TAKE);
        bookBorrowHistory.setBookQueueRecord(bookQueueRecord);
        bookQueueRecord.getBookBorrowHistoryList().add(bookBorrowHistory);
        bookQueueRecordRepository.save(bookQueueRecord);
        return success;
    }
    @Override
    public StatusDTO borrowBook(Long bookId, Long userId) {
        if (Objects.isNull(bookId) || Objects.isNull(userId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        User user = userRepository.getById(userId);
        Book book = bookRepository.getById(bookId);
        if (Objects.isNull(book) || Objects.isNull(user)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if (book.getStatus().equals(BookStatus.NOT_AVAILABLE)) {
            //May be in queue?
            BookQueueRecord bookQueueRecord = bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(book, QueueStatus.ACTIVE);
            if (Objects.isNull(bookQueueRecord)) {
                throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
            }
            BookBorrowHistory bookBorrowHistory = null;
            if (!CollectionUtils.isEmpty(bookQueueRecord.getBookBorrowHistoryList())) {
                bookBorrowHistory = bookQueueRecord.getBookBorrowHistoryList()
                        .stream()
                        .filter(c -> c.getUserId() != null && c.getUserId().getId().equals(userId))
                        .findFirst()
                        .orElse(null);
            }
            //not in queue;
            if (Objects.isNull(bookBorrowHistory)) {
                throw new MLMException(ExceptionCode.BOOK_NOT_AVAILABLE);
            }
            //in queue; Check firstly, book is returned or on someone?
            bookBorrowHistory = null;
            if (!CollectionUtils.isEmpty(bookQueueRecord.getBookBorrowHistoryList())) {
                bookBorrowHistory = bookQueueRecord.getBookBorrowHistoryList()
                        .stream()
                        .filter(c -> c.getStatus().equals(BorrowStatus.WAITING_RETURN))
                        .findFirst()
                        .orElse(null);
            }
            if (Objects.nonNull(bookBorrowHistory)) {
                if(bookBorrowHistory.getUserId().getId().equals(userId)){
                    throw new MLMException(ExceptionCode.BOOK_ALREADY_ON_USER);
                }
                //On someone. not available!
                throw new MLMException(ExceptionCode.BOOK_NOT_RETURNED_YET);
            }
            //first user of the list is this user or not?
//            List<BookBorrowHistory> bookBorrowHistoryList2 = bookQueueRecord.getBookBorrowHistoryList().stream().filter(c -> c.getStatus().equals(BorrowStatus.WAITING_TAKE)).collect(Collectors.toList());
//            bookBorrowHistoryList2.sort(Comparator.comparing(BookBorrowHistory::getCreatedDate));
//            if (bookBorrowHistoryList2.get(0).getUserId().equals(userId)) {
            BookQueueHoldHistoryRecord bookQueueHoldHistoryRecord = bookQueueHoldHistoryRecordRepository.getBookQueueHoldHistoryByBookQueue(bookQueueRecord);
            if (Objects.isNull(bookQueueHoldHistoryRecord)) {
                throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
            }
            if(!bookQueueHoldHistoryRecord.getUserId().equals(userId)){
                //Next person is not this user.
                throw new MLMException(ExceptionCode.BOOK_RESERVED_FOR_SOMEONE);

            }

            //We can give book to user.

            bookBorrowHistory = null;
            bookBorrowHistory = bookQueueRecord.getBookBorrowHistoryList()
                    .stream()
                    .filter(c -> c.getUserId() != null && c.getUserId().getId().equals(userId))
                    .findFirst()
                    .orElse(null);
            bookBorrowHistory.setStatus(BorrowStatus.WAITING_RETURN);
            bookBorrowHistory.setTakeDate(LocalDateTime.now());

            bookQueueRecord.updateBookBorrow(bookBorrowHistory);
            bookQueueRecordRepository.save(bookQueueRecord);
            bookQueueHoldHistoryRecordRepository.delete(bookQueueHoldHistoryRecord);
            return success;
        }
        book.setStatus(BookStatus.NOT_AVAILABLE);
        BookQueueRecord bookQueueRecord = new BookQueueRecord();
        bookQueueRecord.setBookBorrowHistoryList(new ArrayList<>());
        bookQueueRecord.setBookId(book);
        bookQueueRecord.setStatus(QueueStatus.ACTIVE);
        BookBorrowHistory bookBorrowHistory = new BookBorrowHistory();
        bookBorrowHistory.setUserId(user);
        bookBorrowHistory.setStatus(BorrowStatus.WAITING_RETURN);
        bookBorrowHistory.setTakeDate(LocalDateTime.now());
        bookBorrowHistory.setBookQueueRecord(bookQueueRecord);
        bookQueueRecord.getBookBorrowHistoryList().add(bookBorrowHistory);
        bookQueueRecordRepository.save(bookQueueRecord);
        return success;
    }

    @Override
    public StatusDTO takeBackBook(Long bookId) {
        if (Objects.isNull(bookId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Book book = bookRepository.getById(bookId);
        if (Objects.isNull(book)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        BookQueueRecord bookQueueRecord = bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(book, QueueStatus.ACTIVE);
        if (Objects.isNull(bookQueueRecord)) {
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        BookBorrowHistory bookBorrowHistory = null;
        if (!CollectionUtils.isEmpty(bookQueueRecord.getBookBorrowHistoryList())) {
            bookBorrowHistory = bookQueueRecord.getBookBorrowHistoryList()
                    .stream()
                    .filter(c -> c.getUserId() != null && c.getStatus().equals(BorrowStatus.WAITING_RETURN))
                    .findFirst()
                    .orElse(null);
        } else {
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        if (Objects.isNull(bookBorrowHistory)) {
            throw new MLMException(ExceptionCode.THIS_USER_DID_NOT_TAKE_THIS_BOOK);
        }
        try{
            emailRepository.save(new Email().set(bookBorrowHistory.getUserId().getEmail(),
                    "Would you like to give review for "+bookBorrowHistory.getBookQueueRecord().getBookId().getName(),
                    "For better recommendation please give review for "+bookBorrowHistory.getBookQueueRecord().getBookId().getName(),
                    ""));
        }catch (Exception e){

        }
        bookBorrowHistory.setStatus(BorrowStatus.RETURNED);
        bookBorrowHistory.setReturnDate(LocalDateTime.now());
        bookQueueRecord.updateBookBorrow(bookBorrowHistory);
        List<BookBorrowHistory> restOfUsers = bookQueueRecord.getBookBorrowHistoryList().stream().filter(c -> c.getStatus().equals(BorrowStatus.WAITING_TAKE)).collect(Collectors.toList());
        if (restOfUsers.size() == 0) {
            //Queue completed!
            bookQueueRecord.setCompleteDate(LocalDateTime.now());
            bookQueueRecord.setStatus(QueueStatus.END);
            bookQueueRecord.getBookId().setStatus(BookStatus.AVAILABLE);
        } else {
            //Next person.
            restOfUsers.sort(Comparator.comparing(BookBorrowHistory::getCreatedDate));
            BookQueueHoldHistoryRecord bookQueueHoldHistoryRecord = new BookQueueHoldHistoryRecord();
            bookQueueHoldHistoryRecord.setBookQueueRecord(bookQueueRecord);
            bookQueueHoldHistoryRecord.setUserId(restOfUsers.get(0).getUserId().getId());
            bookQueueHoldHistoryRecord.setEndDate(LocalDateTime.now().plusDays(1L).withHour(23).withMinute(30).withSecond(0).withNano(0));
            bookQueueHoldHistoryRecordRepository.save(bookQueueHoldHistoryRecord);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            emailRepository.save(new Email().set(restOfUsers.get(0).getUserId().getEmail(),
                    "The Book is Available \uD83C\uDF89 " ,
                    bookQueueRecord.getBookId().getName()+" is available now! We keep the book for you for a day. We would like to remind you that if you do not take the book by "+ bookQueueHoldHistoryRecord.getEndDate().format(formatter)+", the book will be reserved for the next person in line.",
                    "The Book is Available!"));
        }
        return success;
    }

    @Override
    public StatusDTO givePhysicalCopyCardToUser(String nfcCode, Long userId) {
        if (Objects.isNull(userId) || Objects.isNull(nfcCode)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        User user = userRepository.getById(userId);
        if (Objects.isNull(user)) {
            throw new MLMException(ExceptionCode.USER_NOT_FOUND);
        }
        user.getCopyCard().setNfcCode(nfcCode);
        userRepository.save(user);
        return success;
    }

    public String getUniqueVerfCodeForRoom() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        Boolean flag = true;
        while (flag) {
            code = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                int randomDigit = random.nextInt(10);
                code.append(randomDigit);
            }
            if (Objects.isNull(roomRepository.getByVerfCode(code.toString()))) {
                flag = false;
            }
        }
        return code.toString();
    }

    @Override
    public Boolean checkNowReservationExists(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        if (Objects.isNull(jwtUser) || Objects.isNull(jwtUser.getId())) {
            throw new MLMException(ExceptionCode.UNAUTHORIZED);
        }
        List<RoomReservation> roomReservationList = roomReservationRepository.getRoomReservationByUserId(jwtUser.getId());
        LocalDateTime currentTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        List<RoomReservation> todaysReservations = roomReservationList.stream().filter(c -> (c.getRoomSlot().getDay().getValue() == currentTime.getDayOfWeek().getValue()) && (c.getRoomSlot().getStartHour().getHour() == currentTime.getHour())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(todaysReservations)) {
            for (RoomReservation todaysReservation : todaysReservations) {
                if(!todaysReservation.getApproved()){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    @Override
    public StatusDTO makeReservation(Long roomSlotId) {
        if (Objects.isNull(roomSlotId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }

        RoomSlot roomSlot = roomSlotRepository.getById(roomSlotId);
        if (Objects.isNull(roomSlot)) {
            throw new MLMException(ExceptionCode.ROOMSLOT_NOT_FOUND);
        }
        if (!roomSlot.getAvailable()) {
            throw new MLMException(ExceptionCode.ROOMSLOT_NOT_AVAILABLE);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        if (roomReservationRepository.getRoomReservationByUserId(jwtUser.getId()).size() >= 2) {
            throw new MLMException(ExceptionCode.MAX_RESERVATION_REACHED);
        }

        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setDate(LocalDate.now());
        roomReservation.setUserId(jwtUser.getId());
        roomReservation.setRoomSlot(roomSlot);
        roomSlot.setAvailable(false);
        roomReservation.setApproved(false);

        roomSlotRepository.save(roomSlot);
        roomReservationRepository.save(roomReservation);

        return success;
    }

    @Override
    public StatusDTO cancelReservation(Long roomReservationId) {
        if (Objects.isNull(roomReservationId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        if (Objects.isNull(user)) {
            throw new MLMException(ExceptionCode.UNAUTHORIZED);
        }
        RoomReservation roomReservation = roomReservationRepository.getById(roomReservationId);
        if (Objects.isNull(roomReservation)) {
            throw new MLMException(ExceptionCode.RESERVATION_NOT_FOUND);
        }
        if (user.getRole().equals(Role.LIB) || (roomReservation.getUserId().equals(user.getId()))) {
            RoomSlot roomslot = roomSlotRepository.getById(roomReservation.getRoomSlot().getId());
            roomslot.setAvailable(true);
            roomSlotRepository.save(roomslot);
            roomReservationRepository.delete(roomReservation);
            return success;
        }
        throw new MLMException(ExceptionCode.UNAUTHORIZED);
    }

    @Override
    public StatusDTO generateQRcodeForRoom(Long roomId) {
        if (Objects.isNull(roomId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Room room = roomRepository.getById(roomId);
        if (Objects.isNull(room)) {
            throw new MLMException(ExceptionCode.ROOM_NOT_FOUND);
        }

        Image img = room.getQrImage();
        imageRepository.delete(img);

        String code = getUniqueVerfCodeForRoom();
        ByteArrayOutputStream stream = QRCode
                .from(code)
                .withSize(250, 250)
                .stream();
        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());
        byte[] qrCodeImageData = stream.toByteArray();
        img = new Image();
        img.setImageData(ImageUtil.compressImage(qrCodeImageData));
        img.setName("QR");
        img.setType("png");
        imageRepository.save(img);
        room.setVerfCode(code);
        room.setQrImage(img);
        roomRepository.save(room);
        return success;
    }

    @Override
    public StatusDTO readingNFC(String NFC_no, Long roomId) {
        if (Objects.isNull(roomId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Room room = roomRepository.getById(roomId);
        if (Objects.isNull(room)) {
            throw new MLMException(ExceptionCode.ROOM_NOT_FOUND);
        }
        room.setNFC_no(NFC_no);
        roomRepository.save(room);
        return success;
    }

    @Override
    public StatusDTO approveReservation(String nfcCode, String qrCode) {
        if (!StringUtils.hasText(nfcCode) && !StringUtils.hasText(qrCode)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        if (Objects.isNull(jwtUser) || Objects.isNull(jwtUser.getId())) {
            throw new MLMException(ExceptionCode.UNAUTHORIZED);
        }
        List<RoomReservation> roomReservationList = roomReservationRepository.getRoomReservationByUserId(jwtUser.getId());
        LocalDateTime currentTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        List<RoomReservation> todaysReservations = roomReservationList.stream().filter(c -> (c.getRoomSlot().getDay().getValue() == currentTime.getDayOfWeek().getValue()) && (c.getRoomSlot().getStartHour().getHour() == currentTime.getHour())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(todaysReservations)) {
            if (
                    (StringUtils.hasText(nfcCode) && todaysReservations.get(0).getRoomSlot().getRoom().getNFC_no().equals(nfcCode)) ||
                            (StringUtils.hasText(qrCode) && todaysReservations.get(0).getRoomSlot().getRoom().getVerfCode().equals(qrCode))
            ) {
                todaysReservations.get(0).setApproved(true);
                roomReservationRepository.save(todaysReservations.get(0));
                return success;
            } else {
                throw new MLMException(ExceptionCode.INVALID_CONFIRMATION);
            }
        }

        throw new MLMException(ExceptionCode.RESERVATION_NOT_FOUND);
    }
    @Override
    public StatusDTO createReceiptHistory(Long imageId){
        if (Objects.isNull(imageId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        if (Objects.isNull(user)) {
            throw new MLMException(ExceptionCode.USER_NOT_FOUND);
        }
        Image image = imageRepository.getImageById(imageId);
        if (Objects.isNull(image)) {
            throw new MLMException(ExceptionCode.IMAGE_NOT_FOUND);
        }

        ReceiptHistory receiptHistory = new ReceiptHistory();
        receiptHistory.setUser(user);
        receiptHistory.setImg(image);
        receiptHistory.setApproved(ReceiptStatus.NOT_APPROVED);
        receiptHistory.setBalance(new BigDecimal(0));
        receiptHistoryRepository.save(receiptHistory);
        return success;
    }
    @Override
    public StatusDTO approveReceipt(Long id, BigDecimal balance){
        if(Objects.isNull(id)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        ReceiptHistory receiptHistory = receiptHistoryRepository.getById(id);
        if(Objects.isNull(receiptHistory)){
            throw new MLMException(ExceptionCode.RECEIPT_NOT_FOUND);
        }
        receiptHistory.setBalance(balance);
        receiptHistory.setApproved(ReceiptStatus.APPROVED);
        User user = userRepository.getById(receiptHistory.getUser().getId());
        if(Objects.isNull(user)){
            throw new MLMException(ExceptionCode.USER_NOT_FOUND);
        }
        CopyCard copyCard = copyCardRepository.getByUser(user.getId());
        if(Objects.isNull(copyCard)){
            throw new MLMException(ExceptionCode.COPYCARD_NOT_FOUND);
        }
        boolean debtFlag = false;
        if(Objects.nonNull(user.getDebt()) && user.getDebt().compareTo(BigDecimal.ZERO)> 0){
            //User has debt.
            if(balance.compareTo(user.getDebt()) > 0){
                balance = balance.subtract(user.getDebt());
                copyCard.setBalance((copyCard.getBalance().add(balance)));
            }else if(balance.compareTo(user.getDebt()) <= 0){
                user.setDebt(user.getDebt().subtract(balance));
            }
            debtFlag = true;
        }else{
            copyCard.setBalance((copyCard.getBalance().add(balance)));
        }
        userRepository.save(user);
        copyCardRepository.save(copyCard);
        receiptHistoryRepository.save(receiptHistory);
        StringBuilder content = new StringBuilder();
        content.append("The receipt you uploaded on ");
        content.append(receiptHistory.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        content.append(" has been approved. <br><br>");
        if(debtFlag){
            content.append("Since you previously owed money to the library, the debt was paid with the money you sent. After the transactions, your debt balance became ");
            content.append(user.getDebt().toString());
            content.append(" TL+7 and your copy card balance became ");
            content.append(copyCard.getBalance().toString());
            content.append(" TL.");
        }else{
            content.append("After the transactions, your copy card balance became ");
            content.append(copyCard.getBalance().toString());
            content.append(" TL.");
        }
        emailRepository.save(new Email().set(user.getEmail(),"Receipt approved! \uD83D\uDCB8" ,content.toString(),null));
        return success;
    }

    @Override
    public StatusDTO addReview(AddReviewRequest request){
        if(Objects.isNull(request)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.isNull(request.getBookId())){
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        if(Objects.isNull(request.getStar())){
            throw new MLMException(ExceptionCode.STAR_CANNOT_BE_NULL);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        if (Objects.isNull(user)) {
            throw new MLMException(ExceptionCode.USER_NOT_FOUND);
        }
        BookReview bookReview = bookReviewRepository.getByBookAndUserId(request.getBookId(),user.getId());
        if(Objects.isNull(bookReview)){
            bookReview = new BookReview();
        }
        bookReview.setComment(request.getComment());
        bookReview.setStar(request.getStar());
        Book book = bookRepository.getById(request.getBookId());
        bookReview.setBookId(book);
        bookReview.setUserId(user);
        bookReviewRepository.save(bookReview);
        return success;
    }

    @Override
    public StatusDTO bulkCreateBook(MultipartFile file){
        ExcelReader excelReader = new ExcelReader();
        int counter =0;
        try{
            List<ExcelBookRow> excelBooks = excelReader.parseBookExcel(file);
            for (ExcelBookRow excelBook : excelBooks) {
                if(!CollectionUtils.isEmpty(bookRepository.getBookByIsbn(excelBook.getIsbn()))){
                    continue;
                }
                try{
                    if(Objects.isNull(excelBook.getIsbn()) ||Objects.isNull(excelBook.getCategory()) || Objects.isNull(excelBook.getShelf())){
                        continue;
                    }
                    OpenLibraryBookDetails openLibraryBookDetails = getByISBN(excelBook.getIsbn());
                    if(Objects.isNull(openLibraryBookDetails)){ // If we cannot take information from external system, we have to validate necessary excel infromation.
                        if(Objects.isNull(excelBook.getBookName()) || Objects.isNull(excelBook.getAuthor()) || Objects.isNull(excelBook.getCategory()) || Objects.isNull(excelBook.getShelf())){
                            continue;
                        }
                        BookRequest bookRequest= new BookRequest();
                        bookRequest.setName(excelBook.getBookName());
                        bookRequest.setAuthor(excelBook.getAuthor());
                        bookRequest.setCategory(BookCategory.valueOf(excelBook.getCategory()));
                        bookRequest.setIsbn(excelBook.getIsbn());
                        bookRequest.setPublisher(excelBook.getPublisher());
                        bookRequest.setDescription(excelBook.getDesc());
                        Shelf shelf = shelfRepository.getShelfById(Long.parseLong(excelBook.getShelf()));
                        if (Objects.isNull(shelf)) {
                            throw new MLMException(ExceptionCode.SHELF_NOT_FOUND);
                        }
                        Image image = new Image();
//                    File defaultPic = ResourceUtils.getFile("classpath:defaultpic.jpg");
                        byte[] byteArray = Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQEBLAEsAAD/4QBXRXhpZgAASUkqAAgAAAADAJiCAgANAAAAMgAAABoBBQABAAAAPwAAABsBBQABAAAARwAAAAAAAABBbGFuIENyYXdmb3JkLAEAAAEAAAAsAQAAAQAAAP/hBNFodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iPgoJPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KCQk8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczpwaG90b3Nob3A9Imh0dHA6Ly9ucy5hZG9iZS5jb20vcGhvdG9zaG9wLzEuMC8iIHhtbG5zOklwdGM0eG1wQ29yZT0iaHR0cDovL2lwdGMub3JnL3N0ZC9JcHRjNHhtcENvcmUvMS4wL3htbG5zLyIgICB4bWxuczpHZXR0eUltYWdlc0dJRlQ9Imh0dHA6Ly94bXAuZ2V0dHlpbWFnZXMuY29tL2dpZnQvMS4wLyIgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIiB4bWxuczpwbHVzPSJodHRwOi8vbnMudXNlcGx1cy5vcmcvbGRmL3htcC8xLjAvIiAgeG1sbnM6aXB0Y0V4dD0iaHR0cDovL2lwdGMub3JnL3N0ZC9JcHRjNHhtcEV4dC8yMDA4LTAyLTI5LyIgeG1sbnM6eG1wUmlnaHRzPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvcmlnaHRzLyIgZGM6UmlnaHRzPSJBbGFuIENyYXdmb3JkIiBwaG90b3Nob3A6Q3JlZGl0PSJHZXR0eSBJbWFnZXMiIEdldHR5SW1hZ2VzR0lGVDpBc3NldElEPSIxNTc0ODIwMjkiIHhtcFJpZ2h0czpXZWJTdGF0ZW1lbnQ9Imh0dHBzOi8vd3d3LmlzdG9ja3Bob3RvLmNvbS9sZWdhbC9saWNlbnNlLWFncmVlbWVudD91dG1fbWVkaXVt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");
                        image.setImageData(ImageUtil.compressImage(byteArray));
                        image.setName("DEFAULTPIC");
                        image.setType("jpg");
                        image = imageRepository.save(image);
                        Book book = new Book().fromRequest(bookRequest);
                        book.setStatus(BookStatus.AVAILABLE);
                        book.setShelfId(shelf);
                        book.setImageId(image);
                        bookRepository.save(book);
                        counter++;
                    }else{
                        BookRequest bookRequest = new BookRequest();
                        bookRequest.setName(Objects.nonNull(excelBook.getBookName())?excelBook.getBookName():openLibraryBookDetails.getTitle());
                        bookRequest.setCategory(BookCategory.valueOf(excelBook.getCategory()));
                        bookRequest.setIsbn(excelBook.getIsbn());

                        String desc = "";
                        if(Objects.nonNull(excelBook.getDesc())){
                            desc = excelBook.getDesc();
                        }else if(Objects.nonNull(openLibraryBookDetails.getSubjects())){
                            for (String subject : openLibraryBookDetails.getSubjects()) {
                                desc = desc + " "+subject;
                            }
                        }
                        bookRequest.setDescription(desc);

                        String publisher = "";
                        if(Objects.nonNull(excelBook.getPublisher())){
                            publisher = excelBook.getPublisher();
                        }else if(Objects.nonNull(openLibraryBookDetails.getPublishers())){
                            for (String subject : openLibraryBookDetails.getPublishers()) {
                                publisher = publisher + " "+subject;
                            }
                        }
                        bookRequest.setPublisher(publisher);

                        String author = "";
                        if(Objects.nonNull(excelBook.getAuthor())){
                            author = excelBook.getAuthor();
                        }else if(Objects.nonNull(openLibraryBookDetails.getAuthors())){
                            for (OpenLibraryBookAuthor subject : openLibraryBookDetails.getAuthors()) {
                                if (author.equals("")) {
                                    author = author+" , "+subject.getKey();
                                }else{
                                    author=subject.getKey();
                                }
                            }
                        }
                        bookRequest.setAuthor(author);
                        Shelf shelf = shelfRepository.getShelfById(Long.parseLong(excelBook.getShelf()));
                        if (Objects.isNull(shelf)) {
                            throw new MLMException(ExceptionCode.SHELF_NOT_FOUND);
                        }
                        Image image = new Image();
                        if(Objects.nonNull(openLibraryBookDetails.getImg())){
                            image.setName("FROMGOOGLE");
                            image.setImageData(ImageUtil.compressImage(openLibraryBookDetails.getImg()));
                            image.setType("jpg");
                        }else{
                            byte[] byteArray = Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQEBLAEsAAD/4QBXRXhpZgAASUkqAAgAAAADAJiCAgANAAAAMgAAABoBBQABAAAAPwAAABsBBQABAAAARwAAAAAAAABBbGFuIENyYXdmb3JkLAEAAAEAAAAsAQAAAQAAAP/hBNFodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iPgoJPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KCQk8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczpwaG90b3Nob3A9Imh0dHA6Ly9ucy5hZG9iZS5jb20vcGhvdG9zaG9wLzEuMC8iIHhtbG5zOklwdGM0eG1wQ29yZT0iaHR0cDovL2lwdGMub3JnL3N0ZC9JcHRjNHhtcENvcmUvMS4wL3htbG5zLyIgICB4bWxuczpHZXR0eUltYWdlc0dJRlQ9Imh0dHA6Ly94bXAuZ2V0dHlpbWFnZXMuY29tL2dpZnQvMS4wLyIgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIiB4bWxuczpwbHVzPSJodHRwOi8vbnMudXNlcGx1cy5vcmcvbGRmL3htcC8xLjAvIiAgeG1sbnM6aXB0Y0V4dD0iaHR0cDovL2lwdGMub3JnL3N0ZC9JcHRjNHhtcEV4dC8yMDA4LTAyLTI5LyIgeG1sbnM6eG1wUmlnaHRzPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvcmlnaHRzLyIgZGM6UmlnaHRzPSJBbGFuIENyYXdmb3JkIiBwaG90b3Nob3A6Q3JlZGl0PSJHZXR0eSBJbWFnZXMiIEdldHR5SW1hZ2VzR0lGVDpBc3NldElEPSIxNTc0ODIwMjkiIHhtcFJpZ2h0czpXZWJTdGF0ZW1lbnQ9Imh0dHBzOi8vd3d3LmlzdG9ja3Bob3RvLmNvbS9sZWdhbC9saWNlbnNlLWFncmVlbWVudD91dG1fbWVkaXVt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");
                            image.setImageData(ImageUtil.compressImage(byteArray));
                            image.setName("DEFAULTPIC");
                            image.setType("jpg");
                        }
                        image = imageRepository.save(image);
                        Book book = new Book().fromRequest(bookRequest);
                        book.setStatus(BookStatus.AVAILABLE);
                        book.setShelfId(shelf);
                        book.setImageId(image);
                        bookRepository.save(book);
                        counter++;
                    }
                }catch (Exception e){
                    System.out.println("Error"+e);
                    //do nothing.
                }
            }
        }catch (Exception e){
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        return StatusDTO.builder().statusCode("S").msg(String.valueOf(counter)).build();
    }



    public OpenLibraryBookDetails getByISBN(String isbn){

        String endpoint = "https://openlibrary.org/isbn/"+isbn+".json";
        OpenLibraryBookDetails  dto = null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newUrl = response.headers().map().get("location").get(0);
        request = HttpRequest.newBuilder()
                .uri(URI.create(newUrl))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String body = response.body();
        if(Objects.nonNull(body)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                dto = objectMapper.readValue(body, OpenLibraryBookDetails.class);
                try{
                    for (OpenLibraryBookAuthor author : dto.getAuthors()) {
                        if(Objects.nonNull(author.getKey())){
                            author.setKey(getByRef(author.getKey()));
                        }
                    }
                }catch (Exception e){
                    //Ignore.
                }
            } catch (Exception e) {
                return null;
            }
        }

        String googleEndPoint = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn;
        request = HttpRequest.newBuilder()
                .uri(URI.create(googleEndPoint))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> googleResp = null;
        try {
            googleResp = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String googleBody = googleResp.body();
        GoogleResponse googleResponse;
        if(Objects.nonNull(googleBody)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                googleResponse = objectMapper.readValue(googleBody, GoogleResponse.class);
                try{
                    for (Item item : googleResponse.getItems()) {
                        String url = Objects.nonNull(item.getVolumeInfo().getImageLinks().getThumbnail())?item.getVolumeInfo().getImageLinks().getThumbnail():
                                Objects.nonNull(item.getVolumeInfo().getImageLinks().getSmallThumbnail())?item.getVolumeInfo().getImageLinks().getSmallThumbnail():null;
                        if(Objects.isNull(url)){
                            break;
                        }
                        dto.setImg(getImageBytes(url));
                    }

                }catch (Exception e){
                    //Ignore.
                }
            } catch (Exception e) {
                return null;
            }
        }

        return dto;
    }
    public static byte[] getImageBytes(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        connection.disconnect();
        return outputStream.toByteArray();
    }

    public  String getByRef(String ref){
        String endpoint = "https://openlibrary.org"+ref+".json";
        OpenLibraryBookAuthorDetail dto = null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String body = response.body();
        if(Objects.nonNull(body)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                dto = objectMapper.readValue(body, OpenLibraryBookAuthorDetail.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        return dto.getName();
    }


    @Override
    public LoginResponse changePassword(ChangePasswordRequest request){
        if(Objects.isNull(request)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.isNull(request.getOldPassword())){
            throw new MLMException(ExceptionCode.OLD_PASSWORD_CANNOT_BE_NULL);
        }
        if(Objects.isNull(request.getNewPassword())){
            throw new MLMException(ExceptionCode.PASSWORD_CANNOT_BE_NULL);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        if (Objects.isNull(user)) {
            throw new MLMException(ExceptionCode.UNAUTHORIZED);
        }
        if(user.getPassword().equals(passwordEncoder.encode(request.getOldPassword()))){
            throw new MLMException(ExceptionCode.OLD_PASSWORD_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(user.getUsername());
        userRequest.setPass(user.getPassword());
        return login(userRequest);
    }

    @Override
    public StatusDTO startForgotPasswordProcess(UserRequest request){
        if(Objects.isNull(request)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.isNull(request.getEmail()) && Objects.isNull(request.getUsername())){
            throw new MLMException(ExceptionCode.EMAIL_OR_USERNAME_SHOULD_BE_NOT_EMPTY);
        }
        User user = userRepository.getByEmailOrUsername(request.getEmail(),request.getUsername());
        if(Objects.isNull(user)){
            throw new MLMException(ExceptionCode.USER_NOT_FOUND);
        }
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCode(generateRandomCode(4));
        verificationCode.setVerificationType(VerificationType.RESET_PASSWORD);
        verificationCodeRepository.save(verificationCode);
        mailUtil.sendResetPasswordEmail(user.getEmail(), verificationCode.getCode());
        return  success;
    }

    @Override
    public Boolean checkCodeForResetPassword(String code){
        VerificationCode verificationCode = verificationCodeRepository.getByCode(code);
        if(Objects.isNull(verificationCode)){
            return false;
        }
        return true;
    }

    @Override
    public LoginResponse completeCodeForResetPassword(VerifyChangePasswordRequest request){
        VerificationCode verificationCode = verificationCodeRepository.getByCode(request.getCode());
        if(Objects.isNull(verificationCode)){
            throw new MLMException(ExceptionCode.UNAUTHORIZED);
        }
        User user =verificationCode.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(user.getUsername());
        userRequest.setPass(user.getPassword());
        return login(userRequest);
    }

    @Override
    public StatusDTO createCourse(CreateCourseRequest createCourseRequest){
        if(Objects.isNull(createCourseRequest)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.isNull(createCourseRequest.getName())){
            throw new MLMException(ExceptionCode.COURSE_NAME_CANNOT_BE_NULL);
        }
        if(Objects.isNull(createCourseRequest.getIsPublic())){
            throw new MLMException(ExceptionCode.COURSE_VISIBILITY_CANNOT_BE_NULL);
        }
        if(Objects.isNull(createCourseRequest.getImageId())){
            throw new MLMException(ExceptionCode.COURSE_IMAGE_CANNOT_BE_NULL);
        }
        Image image = imageRepository.getById(createCourseRequest.getImageId());
        if(Objects.isNull(image)){
            throw new MLMException(ExceptionCode.IMAGE_NOT_FOUND);
        }
        Course course = new Course();
        course.setName(createCourseRequest.getName());
        course.setIsPublic(createCourseRequest.getIsPublic());
        course.setImageId(image);
        courseRepository.save(course);
        return success;
    }
    @Override
    public StatusDTO inviteStudent(InviteStudentRequest request){
        User currentUser = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        currentUser = userRepository.getById(jwtUser.getId());
        if(Objects.isNull(request)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.isNull(request.getCourseId())){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        if(StringUtils.isEmpty(request.getStudentNumber())){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        //Now check student id patternç. it should be in 7 digits. only numbers.
        if(!request.getStudentNumber().matches("[0-9]{7}")){
            throw new MLMException(ExceptionCode.INVALID_STUDENT_NUMBER);
        }
        Course course = courseRepository.getById(request.getCourseId());
        if(Objects.isNull(course)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        //Now get first 5 digit of student number and check s/he registered to the system or not.
        String studentNumber = request.getStudentNumber();
        User user = userRepository.findByStudentNumber(studentNumber);
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourse(course);
        courseStudent.setStudentNumber(request.getStudentNumber());
        if(Objects.isNull(user)){
            //Not registered. Send invitation email.
            String title = currentUser.getFullName() + " is inviting you to join the "+course.getName()+" course.";
            StringBuilder content = new StringBuilder();
            content.append("You are invited to join the "+course.getName()+" course. <br>");
            if(Objects.nonNull(webpageLink)){
                content.append("Please click the link below to register to the system. <br>");
                content.append(webpageLink+"/#/register <br>");
            }else{
                content.append("You can download MLM app from the app store and register to the system. <br>");
            }
            emailRepository.save(new Email().set("e"+studentNumber.substring(0,5)+"@metu.edu.tr",title ,content.toString(),null));
        }else{
            courseStudent.setStudent(user);
        }
        courseStudentRepository.save(courseStudent);
        if(Objects.isNull(course.getCourseStudentList())){
            course.setCourseStudentList(new ArrayList<>());
        }
        course.getCourseStudentList().add(courseStudent);
        courseRepository.save(course);
        return success;
    }
    @Override
    public StatusDTO bulkAddStudentToCourse(MultipartFile file, Long courseId){
        if(Objects.isNull(file)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.isNull(courseId)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        Course course = courseRepository.getById(courseId);
        if(Objects.isNull(course)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        ExcelReader excelReader = new ExcelReader();
        List<String> studentNumbers = null;
        try {
            studentNumbers = excelReader.parseStudentExcel(file);
        } catch (IOException e) {
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        if(Objects.isNull(studentNumbers)){
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        List<CourseStudent> courseStudents;
        if(CollectionUtils.isEmpty(course.getCourseStudentList())){
            courseStudents = new ArrayList<>();
        } else {
            courseStudents = course.getCourseStudentList();
        }
        for (String studentNumber : studentNumbers) {
            if(studentNumber.matches("[0-9]{7}")){
                courseStudents.stream().filter(c->c.getStudentNumber().equals(studentNumber)).findFirst().ifPresentOrElse(
                        c->{
                            //Do nothing.
                        },
                        ()->{
                            CourseStudent courseStudent = new CourseStudent();
                            courseStudent.setCourse(course);
                            courseStudent.setStudentNumber(studentNumber);
                            courseStudents.add(courseStudent);
                            User mayBeUser = userRepository.findByStudentNumber(studentNumber);
                            if(Objects.isNull(mayBeUser)) {
                                //Send invitation email.
                                String title = "You are invited to join the " + course.getName() + " course.";
                                StringBuilder content = new StringBuilder();
                                content.append("You are invited to join the " + course.getName() + " course. <br>");
                                if (Objects.nonNull(webpageLink)) {
                                    content.append("Please click the link below to register to the system. <br>");
                                    content.append(webpageLink + "/#/register <br>");
                                } else {
                                    content.append("You can download MLM app from the app store and register to the system. <br>");
                                }
                                emailRepository.save(new Email().set("e" + studentNumber.substring(0, 5) + "@metu.edu.tr", title, content.toString(), null));
                            }else {
                                courseStudent.setStudent(mayBeUser);
                            }
                            courseStudentRepository.save(courseStudent);
                        }
                );
            }
        }
        course.setCourseStudentList(courseStudents);
        courseRepository.save(course);
        return success;
    }

    @Override
    public StatusDTO uploadCourseMaterial(MultipartFile file, Long courseId, String name) throws IOException {
        if(Objects.isNull(file)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.isNull(courseId)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        Course course = courseRepository.getById(courseId);
        if(Objects.isNull(course)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        CourseMaterial courseMaterial = new CourseMaterial();
        courseMaterial.setData(ImageUtil.compressImage(file.getBytes()));
        courseMaterial.setFileName(file.getOriginalFilename());
        courseMaterial.setExtension(file.getContentType());
        courseMaterial.setName(name);
        courseMaterial.setCourse(course);
        courseMaterialRepository.save(courseMaterial);
        return success;
    }

    @Override
    public StatusDTO deleteCourseMaterial(Long materialId){
        if(Objects.isNull(materialId)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        CourseMaterial courseMaterial = courseMaterialRepository.getById(materialId);
        if(Objects.isNull(courseMaterial)){
            throw new MLMException(ExceptionCode.MATERIAL_NOT_FOUND);
        }
        Course course = courseMaterial.getCourse();
        course.getCourseMaterialList().remove(courseMaterial);
        courseRepository.save(course);
        courseMaterialRepository.delete(courseMaterial);
        return success;
    }
    @Override
    public StatusDTO removeStudentFromCourse(Long courseId, Long courseStudentId){
        if(Objects.isNull(courseId)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        if(Objects.isNull(courseStudentId)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Course course = courseRepository.getById(courseId);
        if(Objects.isNull(course)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        CourseStudent courseStudent = courseStudentRepository.getById(courseStudentId);
        if(Objects.isNull(courseStudent)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        course.getCourseStudentList().remove(courseStudent);
        courseRepository.save(course);
        courseStudentRepository.delete(courseStudent);
        return success;
    }
    @Override
    public StatusDTO finishCourseTerm(Long courseId){
        if(Objects.isNull(courseId)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }

        Course course = courseRepository.getById(courseId);
        if(Objects.isNull(course)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        course.setIsPublic(false);
        courseStudentRepository.deleteAll(course.getCourseStudentList());
        course.setCourseStudentList(new ArrayList<>());
        courseRepository.save(course);

        return success;
    }
    @Override
    public StatusDTO bulkRemoveStudentFromCourse(Long courseId, MultipartFile file){
        if(Objects.isNull(courseId)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        if(Objects.isNull(file)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Course course = courseRepository.getById(courseId);
        if(Objects.isNull(course)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        ExcelReader excelReader = new ExcelReader();
        List<String> studentNumbers = null;
        try {
            studentNumbers = excelReader.parseStudentExcel(file);
        } catch (IOException e) {
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        if(Objects.isNull(studentNumbers)){
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        List<CourseStudent> courseStudents = course.getCourseStudentList();
        for (String studentNumber : studentNumbers) {
            courseStudents.stream().filter(c->c.getStudentNumber().equals(studentNumber)).findFirst().ifPresentOrElse(
                    c->{
                        course.getCourseStudentList().remove(c);
                        courseStudentRepository.delete(c);
                    },
                    ()->{
                        //Do nothing.
                    }
            );
        }
        courseRepository.save(course);
        return success;
    }

    @Override
    public StatusDTO addToFavorite(Long bookId){
        if(Objects.isNull(bookId)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Book book = bookRepository.getById(bookId);
        if(Objects.isNull(book)){
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        if(Objects.nonNull(favoriteRepository.findByUserIdAndBookId(jwtUser.getId(),bookId))){
            throw new MLMException(ExceptionCode.ALREADY_FAVORITED);
        }
        User user = userRepository.getById(jwtUser.getId());
        Favorite favorite = new Favorite();
        favorite.setUserId(user);
        favorite.setBookId(book);
        favoriteRepository.save(favorite);
        return success;
    }

    @Override
    public StatusDTO addEbook(Long bookId, MultipartFile file) throws IOException {
        if(Objects.isNull(bookId) || Objects.isNull(file)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(!(file.getOriginalFilename().endsWith(".pdf") || file.getOriginalFilename().endsWith(".epub"))){
            throw new MLMException(ExceptionCode.UNSUPPORTED_FILE);
        }
        Book book = bookRepository.getById(bookId);
        if(Objects.isNull(book)){
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }

        Ebook ebook = ebookRepository.findByBookId(bookId);

        if(Objects.isNull(ebook)){
            ebook = new Ebook();
            ebook.setBook(book);
        }
        ebook.setData(ImageUtil.compressImage(file.getBytes()));
        ebook.setFileName(file.getOriginalFilename());
        ebook.setExtension(file.getContentType());
        ebookRepository.save(ebook);
        return success;
    }

    @Override
    public StatusDTO rejectReceipt(Long receiptId){
        if(Objects.isNull(receiptId)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        ReceiptHistory receiptHistory = receiptHistoryRepository.getById(receiptId);
        if(Objects.isNull(receiptHistory)){
            throw new MLMException(ExceptionCode.RECEIPT_NOT_FOUND);
        }
        receiptHistory.setApproved(ReceiptStatus.REJECTED);
        receiptHistoryRepository.save(receiptHistory);
        return success;
    }

    @Override
    public StatusDTO removeFavorite(Long bookId){
        if(Objects.isNull(bookId)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Book book = bookRepository.getById(bookId);
        if(Objects.isNull(book)){
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();

        Favorite favorite = favoriteRepository.findByUserIdAndBookId(jwtUser.getId(),bookId);
        if(Objects.isNull(favorite)){
            throw new MLMException(ExceptionCode.FAVORITE_NOT_FOUND);
        }
        favoriteRepository.delete(favorite);
        return success;
    }

}
