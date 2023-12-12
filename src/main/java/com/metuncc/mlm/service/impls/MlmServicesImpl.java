package com.metuncc.mlm.service.impls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.api.response.ReceiptHistoryDTOListResponse;
import com.metuncc.mlm.dto.StatusDTO;
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
import lombok.AllArgsConstructor;
import net.glxn.qrgen.QRCode;
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
import java.io.IOException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class MlmServicesImpl implements MlmServices {

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
    private final StatusDTO success = StatusDTO.builder().statusCode("S").msg("Success!").build();
    private final StatusDTO error = StatusDTO.builder().statusCode("E").msg("Error!").build();


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
                StringUtils.isEmpty(userRequest.getEmail())
        ) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if (!userRequest.getEmail().endsWith("@metu.edu.tr")) {
            throw new MLMException(ExceptionCode.ONLY_METU);
        }
        if (Objects.nonNull(userRepository.findByUsername(userRequest.getUsername())) ||
                Objects.nonNull(userRepository.findByEmail(userRequest.getEmail()))) {
            throw new MLMException(ExceptionCode.USERNAME_ALREADY_TAKEN);
        }
        User user = new User().fromRequest(userRequest);
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(userRequest.getPass()));
        user = userRepository.save(user);
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCode(generateRandomCode(4));
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
        VerificationCode verificationCode = verificationCodeRepository.getByUser(user);
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
        book = book.fromRequest(request);
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
        for (RoomSlotDays day : days) {
            for (int i = Integer.valueOf("08"); i <= Integer.valueOf("23"); i++) {
                LocalTime localTimeStart = LocalTime.of(i, 0, 0, 0);
                LocalTime localTimeEnd = LocalTime.of(i, 59, 0, 0);
                RoomSlot roomSlot = new RoomSlot();
                roomSlot.setStartHour(localTimeStart);
                roomSlot.setEndHour(localTimeEnd);
                roomSlot.setDay(day);
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
            if (!CollectionUtils.isEmpty(room.getRoomSlotList())) {
                roomSlotRepository.deleteAll(room.getRoomSlotList());
            }
            room.setRoomSlotList(new ArrayList<>());
            for (int i = Integer.valueOf(start); i <= Integer.valueOf(end); i++) {
                LocalTime localTimeStart = LocalTime.of(i, 0, 0, 0);
                LocalTime localTimeEnd = LocalTime.of(i, 59, 0, 0);
                RoomSlot roomSlot = new RoomSlot();
                roomSlot.setStartHour(localTimeStart);
                roomSlot.setEndHour(localTimeEnd);
                roomSlot.setDay(day);
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
        //not in queue;
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
                //On someone. not available!
                throw new MLMException(ExceptionCode.BOOK_NOT_RETURNED_YET);
            }
            //first user of the list is this user or not?
            List<BookBorrowHistory> bookBorrowHistoryList2 = bookQueueRecord.getBookBorrowHistoryList().stream().filter(c -> c.getStatus().equals(BorrowStatus.WAITING_TAKE)).collect(Collectors.toList());
            bookBorrowHistoryList2.sort(Comparator.comparing(BookBorrowHistory::getCreatedDate));
            if (bookBorrowHistoryList2.get(0).getUserId().equals(userId)) {
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
            bookQueueRecord.updateBookBorrow(bookBorrowHistory);
            bookQueueRecordRepository.save(bookQueueRecord);
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
        bookBorrowHistory.setBookQueueRecord(bookQueueRecord);
        bookQueueRecord.getBookBorrowHistoryList().add(bookBorrowHistory);
        bookQueueRecordRepository.save(bookQueueRecord);
        return success;
    }

    @Override
    public StatusDTO takeBackBook(Long bookId, Long userId) {
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
                    .filter(c -> c.getUserId() != null && c.getUserId().getId().equals(userId))
                    .findFirst()
                    .orElse(null);
        } else {
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
        if (Objects.isNull(bookBorrowHistory)) {
            throw new MLMException(ExceptionCode.THIS_USER_DID_NOT_TAKE_THIS_BOOK);
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
            mailUtil.sendCustomEmail(restOfUsers.get(0).getUserId().getEmail(),
                    "The Book is Available \uD83C\uDF89 ",
                    bookQueueRecord.getBookId().getName()+" is available now! We keep the book for you for a day. We would like to remind you that if you do not take the book by "+ bookQueueHoldHistoryRecord.getEndDate().format(formatter)+", the book will be reserved for the next person in line.",
                    "The Book is Available!");
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
        receiptHistory.setApproved(false);
        receiptHistory.setBalance(new BigDecimal(0));
        receiptHistoryRepository.save(receiptHistory);
        return success;
    }
}
