package com.metuncc.mlm.service.impls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.request.GetReceiptRequest;
import com.metuncc.mlm.api.response.*;
import com.metuncc.mlm.api.request.FindUserRequest;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.dto.google.GoogleResponse;
import com.metuncc.mlm.dto.google.Item;
import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.*;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.repository.ImageRepository;
import com.metuncc.mlm.repository.RoomRepository;
import com.metuncc.mlm.repository.ShelfRepository;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.repository.specifications.BookSpecification;
import com.metuncc.mlm.repository.specifications.UserSpecification;
import com.metuncc.mlm.security.JwtUserDetails;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.utils.ImageUtil;
import com.metuncc.mlm.utils.excel.BookExcelWriter;
import com.metuncc.mlm.utils.excel.CourseStudentExcelWriter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@AllArgsConstructor
public class MlmQueryServicesImpl implements MlmQueryServices {

    private final UserRepository userRepository;
    private ShelfRepository shelfRepository;
    private RoomRepository roomRepository;
    private ImageRepository imageRepository;
    private BookRepository bookRepository;
    private CopyCardRepository copyCardRepository;
    private RoomSlotRepository roomSlotRepository;
    private ReceiptHistoryRepository receiptHistoryRepository;
    private BookQueueRecordRepository bookQueueRecordRepository;
    private BookBorrowHistoryRepository bookBorrowHistoryRepository;
    private BookQueueHoldHistoryRecordRepository bookQueueHoldHistoryRecordRepository;
    private StatisticsRepository statisticsRepository;
    private BookReviewRepository bookReviewRepository;
    private RoomReservationRepository roomReservationRepository;
    private CourseRepository courseRepository;
    private CourseMaterialRepository courseMaterialRepository;
    private CourseStudentRepository courseStudentRepository;


    @Override
    public UserDTO getOneUserByUserName(String username) {
        return userRepository.findByUsername(username).toDTO();
    }


    @Override
    public ShelfDTO getShelfById(Long id) {
        if (Objects.isNull(id)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Shelf shelf = shelfRepository.getById(id);
        if (Objects.isNull(shelf)) {
            throw new MLMException(ExceptionCode.SHELF_NOT_FOUND);
        }
        return shelf.toDTO();
    }

    @Override
    public ShelfDTOListResponse getAllShelfs() {
        ShelfDTOListResponse response = new ShelfDTOListResponse();
        response.setShelfDTOList(shelfRepository.findAll().stream().map(Shelf::toDTO).collect(Collectors.toList()));
        return response;
    }

    @Override
    public ImageDTO getImageById(Long id) {
        Image dbImage = imageRepository.getImageById(id);
        if (Objects.nonNull(dbImage)) {
            ImageDTO dto = ImageDTO
                    .builder()
                    .name(dbImage.getName())
                    .imageData(ImageUtil.decompressImage(dbImage.getImageData()))
                    .build();
            dto.setId(id);
            return dto;
        } else {
            return null;
        }
    }

    @Override
    public BookDTO getBookById(Long id) {
        if (Objects.isNull(id)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Book book = bookRepository.getById(id);
        if (Objects.isNull(book)) {
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        BookReviewDTO reviewDTO = new BookReviewDTO();
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
            BookReview bookReview = bookReviewRepository.getByBookAndUserId(id,jwtUser.getId());
            if(Objects.nonNull(bookReview)){
                return book.toDTOWithReview(bookReview.toDTO());
            }
        }catch (Exception e){
            //do nothing.
        }
        return book.toDTO();
    }

    @Override
    public BookDTOListResponse getBooksByShelfId(Long shelfId) {
        BookDTOListResponse response = new BookDTOListResponse();
        response.setBookDTOList(bookRepository.getBooksByShelfId(shelfId).stream().map(Book::toDTO).collect(Collectors.toList()));
        return response;
    }


    @Override
    public UserDTOListResponse getUsersBySpecifications(FindUserRequest request) {
        if (Objects.isNull(request)) {
            request = new FindUserRequest();
            request.setSize(7);
            request.setPage(0);
        }
        UserSpecification userSpecification = new UserSpecification(
                request.getRole(),
                request.getFullName(),
                request.getUsername(),
                request.getVerified(),
                request.getEmail()
        );
        if (Objects.isNull(request.getSize()) || Objects.isNull(request.getPage())) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<User> usersPage = userRepository.findAll(userSpecification, pageable);
        List<UserDTO> dtos = usersPage.getContent().stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
        UserDTOListResponse response = new UserDTOListResponse();
        response.setUserDTOList(dtos);
        response.setTotalPage(usersPage.getTotalPages());
        response.setTotalResult(usersPage.getTotalElements());
        response.setSize(request.getSize());
        response.setPage(request.getPage());
        return response;
    }

    @Override
    public UserDTO getUserDetails() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
            User user = userRepository.getById(jwtUser.getId());
            if (Objects.nonNull(user)) {
                return user.toDTO();
            }
        } catch (Exception e) {
            throw new MLMException(ExceptionCode.SESSION_EXPERIED_PLEASE_LOGIN);
        }
        throw new MLMException(ExceptionCode.SESSION_EXPERIED_PLEASE_LOGIN);
    }

    @Override
    public BookDTOListResponse getBooksBySpecification(FindBookRequest request) {
        if (Objects.isNull(request)) {
            request = new FindBookRequest();
            request.setPage(0);
            request.setSize(7);
        }
        BookSpecification bookSpecification = new BookSpecification(
                request.getName(),
                request.getAuthor(),
                request.getPublisher(),
                request.getDescription(),
                request.getIsbn(),
                request.getPublicationDate(),
                request.getBarcode(),
                request.getCategory(),
                request.getStatus()
        );
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Book> bookPage = bookRepository.findAll(bookSpecification, pageable);
        List<BookDTO> bookDTOS = bookPage.getContent().stream()
                .map(Book::toDTO)
                .collect(Collectors.toList());
        for (BookDTO bookDTO : bookDTOS) {
            BigDecimal avg = bookReviewRepository.getAvgByBookId(bookDTO.getId());
            bookDTO.setAveragePoint(avg);
        }
        BookDTOListResponse response = new BookDTOListResponse();
        response.setBookDTOList(bookDTOS);
        response.setTotalPage(bookPage.getTotalPages());
        response.setTotalResult(bookPage.getTotalElements());
        response.setSize(request.getSize());
        response.setPage(request.getPage());
        return response;

    }

    @Override
    public RoomDTO getRoomById(Long id) {
        if (Objects.isNull(id)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Room room = roomRepository.getById(id);
        if (Objects.isNull(room)) {
            throw new MLMException(ExceptionCode.ROOM_NOT_FOUND);
        }

        return room.toDTO();
    }

    @Override
    public RoomDTOListResponse getRooms() {
        return new RoomDTOListResponse(roomRepository.findAll().stream().map(Room::toDTO).collect(Collectors.toList()));
    }

    @Override
    public CopyCardDTO getCopyCardDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();

        return copyCardRepository.getByUser(jwtUser.getId()).toDTO();
    }
    @Override
    public OpenLibraryBookDetails getBookDetailsFromExternalWithISBN(String isbn){

        return getByISBN(isbn);
    }
    public  OpenLibraryBookDetails getByISBN(String isbn){

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
    public List<BookCategoryEnumDTO> getAllBookCategories(){
        List<BookCategoryEnumDTO> dtoList = new ArrayList<>();

        for (BookCategory category : BookCategory.values()) {
            BookCategoryEnumDTO dto = new BookCategoryEnumDTO();
            dto.setStr(category.toString());
            dto.setEnumValue(category.name());
            dtoList.add(dto);
        }
        return dtoList;
    }
    @Override
    public ReceiptHistoryDTOListResponse getReceiptsOfUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();

        ReceiptHistoryDTOListResponse response = new ReceiptHistoryDTOListResponse();
        response.setReceiptHistoryDTOList(receiptHistoryRepository.getByUserId(jwtUser.getId()).stream().map(ReceiptHistory::toDTO).collect(Collectors.toList()));
        return response;
    }
    @Override
    public ReceiptHistoryDTOListResponse getReceipts(){
        ReceiptHistoryDTOListResponse response = new ReceiptHistoryDTOListResponse();
        response.setReceiptHistoryDTOList(receiptHistoryRepository.findAll().stream().map(ReceiptHistory::toDTO).collect(Collectors.toList()));
        return response;
    }
    @Override
    public ReceiptHistoryDTOListResponse getReceiptsByStatus(GetReceiptRequest request){
        if(Objects.isNull(request)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.isNull(request.getPage()) ||Objects.isNull(request.getSize())){
            request.setPage(0);
            request.setPage(10);
        }
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<ReceiptHistory> receiptPage = receiptHistoryRepository.getByStatus(request.getIsApproved(), pageable);
        List<ReceiptHistoryDTO> receiptDTOs = receiptPage.getContent().stream().map(ReceiptHistory::toDTO).collect(Collectors.toList());
        ReceiptHistoryDTOListResponse response = new ReceiptHistoryDTOListResponse();
        response.setReceiptHistoryDTOList(receiptDTOs);
        response.setTotalPage(receiptPage.getTotalPages());
        response.setTotalResult(receiptPage.getTotalElements());
        return response;
    }
    @Override
    public ReceiptHistoryDTOListResponse getReceiptsByUser(Long id){
        ReceiptHistoryDTOListResponse response = new ReceiptHistoryDTOListResponse();
        response.setReceiptHistoryDTOList(receiptHistoryRepository.getByUserId(id).stream().map(ReceiptHistory::toDTO).collect(Collectors.toList()));
        return response;
    }
    @Override
    public ReceiptHistoryDTOHashMapResponse getReceiptsHashMap(){
        ReceiptHistoryDTOHashMapResponse response = new ReceiptHistoryDTOHashMapResponse();
        HashMap<Long, List<ReceiptHistoryDTO>> receiptHistoryHashMap = new HashMap<>();
        List<ReceiptHistoryDTO> allReceipts = receiptHistoryRepository.findAll().stream().map(ReceiptHistory::toDTO).toList();
        for (ReceiptHistoryDTO receipt : allReceipts) {
            Long userId = receipt.getUserId();
            receiptHistoryHashMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(receipt);
        }
        response.setReceiptHistoryHashMap(receiptHistoryHashMap);
        return response;
    }
    @Override
    public StatisticsDTO getStatistics(){
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setTotalUserCount(userRepository.totalUserCount());
        statisticsDTO.setTotalBookCount(bookRepository.totalBookCount());
        statisticsDTO.setAvailableBookCount(bookRepository.bookCountByAvailability(BookStatus.AVAILABLE));
        statisticsDTO.setUnavailableBookCount(bookRepository.bookCountByAvailability(BookStatus.NOT_AVAILABLE));
        statisticsDTO.setSumOfBalance(copyCardRepository.totalBalance());
        statisticsDTO.setSumOfDebt(userRepository.totalDebt());
        statisticsDTO.setQueueCount(bookQueueRecordRepository.getBookQueueRecordByStatus(QueueStatus.ACTIVE));

        LocalDateTime today = LocalDateTime.now();
        statisticsDTO.setDay(today.getDayOfWeek());
        statisticsDTO.setDayDesc(today.getDayOfWeek().name());
        statisticsDTO.setDayInt(today.getDayOfWeek().getValue());
        statisticsDTO.setId(999L);
        return statisticsDTO;
    }

    @Override
    public UserNamesDTOListResponse getAllUsers(){
        List<UserNamesDTO> dtos = new ArrayList<>();
        List<User> users = userRepository.findAllByRoles(Role.USER);
        for (User user : users) {
            UserNamesDTO dto = new UserNamesDTO();
            dto.setDisplayName(user.getFullName()+" - "+user.getUsername());
            dto.setId(user.getId());
            dtos.add(dto);
        }
        UserNamesDTOListResponse response = new UserNamesDTOListResponse();
        response.setDtoList(dtos);
        return response;
    }
    @Override
    public MyBooksDTOListResponse getMyBooks(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        List<MyBooksDTO> dtos = new ArrayList<>();
        List<BookBorrowHistory> borrows = bookBorrowHistoryRepository.getByUserIdandStatus(jwtUser.getId(), BorrowStatus.WAITING_RETURN);
        for (int i = 0; i< borrows.size(); i++){
            MyBooksDTO dto = new MyBooksDTO();
            dto.setBook(borrows.get(i).getBookQueueRecord().getBookId().toDTO());
            dto.setDays(abs(15-DAYS.between(borrows.get(i).getCreatedDate(), LocalDateTime.now())));
            if(15-DAYS.between(borrows.get(i).getCreatedDate(), LocalDateTime.now())<0){
                dto.setIsLate(true);
            }
            else{
                dto.setIsLate(false);
            }
            dtos.add(dto);
        }
        MyBooksDTOListResponse response = new MyBooksDTOListResponse();
        response.setMyBooksDTOList(dtos);
        return response;
    }

    @Override
    public StatusDTO getQueueStatusBasedOnBook(Long bookId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        if (Objects.isNull(bookId)) {
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Book book = bookRepository.getById(bookId);
        if (Objects.isNull(book)) {
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        if(book.getStatus().equals(BookStatus.AVAILABLE)){
           return  StatusDTO.builder().statusCode("S").msg("This book is available now! You can borrow from librarian!").build();
        }
        BookQueueRecord bookQueueRecord = bookQueueRecordRepository.getBookQueueRecordByBookIdAndDeletedAndStatus(book,QueueStatus.ACTIVE);
        if(Objects.isNull(bookQueueRecord)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }

        List<BookBorrowHistory> bookBorrowHistoriesForUser = bookQueueRecord.getBookBorrowHistoryList().stream().filter(c->c.getUserId().getId().equals(user.getId())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(bookBorrowHistoriesForUser)){
            BookBorrowHistory bookBorrowHistory = bookBorrowHistoriesForUser.get(0);
            if(bookBorrowHistory.getStatus().equals(BorrowStatus.DID_NOT_TAKEN)){
                return  StatusDTO.builder().statusCode("DID_NOT_TAKEN").msg("You missed your turn :( You must wait for the current queue to end before you can queue again.").build();
            }
            if(bookBorrowHistory.getStatus().equals(BorrowStatus.WAITING_RETURN)){
                return  StatusDTO.builder().statusCode("WAITING_RETURN").msg("You already have the book. If you think there is a mistake, you can consult the librarian.").build();
            }
            if(bookBorrowHistory.getStatus().equals(BorrowStatus.RETURNED)){
                return  StatusDTO.builder().statusCode("RETURNED").msg("You already returned this book! If you think there is a mistake, you can consult the librarian.").build();
            }
        }

        List<BookBorrowHistory> bookBorrowHistoryList = bookQueueRecord.getBookBorrowHistoryList().stream().filter(c->c.getStatus().equals(BorrowStatus.WAITING_TAKE)).collect(Collectors.toList());
        Collections.sort(bookBorrowHistoryList, Comparator.comparingLong(BookBorrowHistory::getId));
        List<BookBorrowHistory> listofuser = bookBorrowHistoryList.stream().filter(c->c.getUserId().getId().equals(user.getId())).collect(Collectors.toList());
        if(listofuser.size()>0){
            int foundIndex = -1;
            for (int i = 0; i < bookBorrowHistoryList.size(); i++) {
                if (Objects.equals(bookBorrowHistoryList.get(i).getId(), listofuser.get(0).getId())) {
                    foundIndex = i;
                    break;
                }
            }
            foundIndex++;
            return  StatusDTO.builder().statusCode("ALREADY_QUEUE").msg("You are already in queue :) There are "+ foundIndex +" people before you. We will inform you when your turn comes.").build();
        }else{
            return  StatusDTO.builder().statusCode("S").msg("This book is not available but don't worry, you can enter queue. When it's your turn, we'll reserve the book for you.").build();

        }
    }
    @Override
    public QueueDetailDTO getQueueStatusBasedOnBookForLibrarian(Long bookId){
        Book book = bookRepository.getById(bookId);
        if (Objects.isNull(book)) {
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        List<BookQueueRecord> bookQueueRecordList = bookQueueRecordRepository.getBookQueueRecordByBookId(book);
        if(CollectionUtils.isEmpty(bookQueueRecordList)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        BookQueueRecord bookQueueRecord = bookQueueRecordList.get(0);
        QueueDetailDTO queueDetailDTO = new QueueDetailDTO();
        queueDetailDTO.setBookDTO(bookQueueRecord.getBookId().toDTO());
        queueDetailDTO.setStatus(bookQueueRecord.getStatus());
        queueDetailDTO.setStatusDesc(bookQueueRecord.getStatus().toString());
        queueDetailDTO.setMembers(new ArrayList<>());
        List<BookBorrowHistory> bookBorrowHistoryList = bookQueueRecord.getBookBorrowHistoryList();
        Collections.sort(bookBorrowHistoryList, Comparator.comparingLong(BookBorrowHistory::getId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Boolean waitToReturnFlag = false;
        for (BookBorrowHistory bookBorrowHistory : bookBorrowHistoryList) {
            QueueMembersDTO dto = new QueueMembersDTO();
            dto.setUserDTO(bookBorrowHistory.getUserId().toDTO());
            dto.setEnterDate(bookBorrowHistory.getCreatedDate().format(formatter));
            dto.setTakeDate(Objects.nonNull(bookBorrowHistory.getTakeDate())?bookBorrowHistory.getTakeDate().format(formatter):"--");
            dto.setReturnDate(Objects.nonNull(bookBorrowHistory.getReturnDate())?bookBorrowHistory.getReturnDate().format(formatter):"--");
            dto.setStatus(bookBorrowHistory.getStatus());
            dto.setStatusDesc(bookBorrowHistory.getStatus().toString());
            queueDetailDTO.getMembers().add(dto);
            if(bookBorrowHistory.getStatus().equals(BorrowStatus.WAITING_RETURN)){
                waitToReturnFlag = true;
            }
        }
        if(!waitToReturnFlag && queueDetailDTO.getStatus().equals(QueueStatus.ACTIVE)){
            BookQueueHoldHistoryRecord bookQueueHoldHistoryRecord = bookQueueHoldHistoryRecordRepository.getBookQueueHoldHistoryByBookQueue(bookQueueRecord);
            BookQueueHoldHistoryRecordDTO dto = new BookQueueHoldHistoryRecordDTO();
            dto.setId(bookQueueHoldHistoryRecord.getId());
            dto.setUserId(bookQueueHoldHistoryRecord.getUserId());
            dto.setEndDate(bookQueueHoldHistoryRecord.getEndDate().format(formatter));
            queueDetailDTO.setHoldDTO(dto);
        }
            queueDetailDTO.setHoldFlag(queueDetailDTO.getStatus().equals(QueueStatus.ACTIVE) && !waitToReturnFlag);
        queueDetailDTO.setStartDate(bookQueueRecord.getCreatedDate().format(formatter));
        return queueDetailDTO;
    }


    @Override
    public List<StatisticsDTO> getStatisticsForChart(){
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setTotalUserCount(userRepository.totalUserCount());
        statisticsDTO.setTotalBookCount(bookRepository.totalBookCount());
        statisticsDTO.setAvailableBookCount(bookRepository.bookCountByAvailability(BookStatus.AVAILABLE));
        statisticsDTO.setUnavailableBookCount(bookRepository.bookCountByAvailability(BookStatus.NOT_AVAILABLE));
        statisticsDTO.setSumOfBalance(copyCardRepository.totalBalance());
        statisticsDTO.setSumOfDebt(userRepository.totalDebt());
        statisticsDTO.setQueueCount(bookQueueRecordRepository.getBookQueueRecordByStatus(QueueStatus.ACTIVE));
        statisticsDTO.setId(9999L);
        LocalDateTime today = LocalDateTime.now();
        statisticsDTO.setDay(today.getDayOfWeek());
        statisticsDTO.setDayDesc(today.getDayOfWeek().name());
        statisticsDTO.setDayInt(today.getDayOfWeek().getValue());
        List<Statistics> statistics = statisticsRepository.findAll();
        Collections.sort(statistics, Comparator.comparingLong(Statistics::getId));
        List<StatisticsDTO> statisticsDTOS  = statistics.stream().filter(c->!c.getDay().equals(today.getDayOfWeek())).collect(Collectors.toList()).stream().map(Statistics::toDTO).collect(Collectors.toList());
        statisticsDTOS.add(statisticsDTO);
        Collections.sort(statisticsDTOS, Comparator.comparingLong(StatisticsDTO::getId));
        List<StatisticsDTO> sortedStatistics = statisticsDTOS.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        return sortedStatistics;
    }
    @Override
    public List<BookReviewDTO> getBookReviewsByBookId(Long id){
        if(Objects.isNull(id)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookReview> bookReviews = bookReviewRepository.getByBookId(id,pageable);
        return bookReviews.stream().map(BookReview::toDTO).collect(Collectors.toList());
    }

    @Override
    public byte[] getExcel(){
        try{
            List<Shelf> shelfs = shelfRepository.findAll();
            List<ShelfDTO> shelfDTOList = new ArrayList<>();
            for (Shelf shelf : shelfs) {
                ShelfDTO shelfDTO = new ShelfDTO();
                shelfDTO.setId(shelf.getId());
                shelfDTO.setFloor(shelf.getFloor());
                shelfDTOList.add(shelfDTO);
            }
            BookExcelWriter bookExcelWriter = BookExcelWriter.builder()
                    .categoryEnumDTOList(getAllBookCategories())
                    .shelfDTOList(shelfDTOList)
                    .build();
            return bookExcelWriter.create();
        }catch (Exception e){
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
    }
    @Override
    public RoomSlotDTOListResponse getRoomSlotsById(Long roomId){
        if(Objects.isNull(roomRepository.getById(roomId))){
            throw new MLMException(ExceptionCode.ROOM_NOT_FOUND);
        }
        List<RoomSlot> roomSlotList = roomSlotRepository.getRoomSlotsByRoomId(roomId);
        return new RoomSlotDTOListResponse(roomSlotList.stream().map(RoomSlot::toDto).collect(Collectors.toList()));

    }
    @Override
    public RoomSlotWithResDTOListResponse getRoomSlotsWithReservationById(Long roomId){
        if(Objects.isNull(roomRepository.getById(roomId))){
            throw new MLMException(ExceptionCode.ROOM_NOT_FOUND);
        }
        List<RoomSlotWithResDTO> roomSlotDTOList =new ArrayList<>();
        List<RoomSlot> roomSlotList = roomSlotRepository.getRoomSlotsByRoomId(roomId);
        for (RoomSlot roomSlot : roomSlotList) {
            if(!roomSlot.getAvailable()){
                RoomSlotWithResDTO dto = roomSlot.toResDto();
                RoomReservation reservation = roomReservationRepository.findByRoomSlot(roomSlot.getId());
                if(Objects.nonNull(reservation)){
                    dto.setReservationDTO(reservation.toDTO());
                }
                roomSlotDTOList.add(dto);
            }else{
                roomSlotDTOList.add(roomSlot.toResDto());
            }
        }
        return new RoomSlotWithResDTOListResponse(roomSlotDTOList);

    }

    @Override
    public DepartmentDTOListResponse getDeps() {
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        for (Department department : Department.values()) {
            DepartmentDTO departmentDTO = new DepartmentDTO();
            departmentDTO.setDepartment(department);
            departmentDTO.setDepartmentString(department.toString());
            departmentDTOList.add(departmentDTO);
        }
        DepartmentDTOListResponse response = new DepartmentDTOListResponse();
        response.setDepartmentDTOList(departmentDTOList);
        return response;
    }

    @Override
    public StatusDTO updateRoleOfUser(Long userId, Role role){
        if(Objects.isNull(userId) ||Objects.isNull(role)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        User user = userRepository.getById(userId);
        if(Objects.isNull(user)){
            throw new MLMException(ExceptionCode.USER_NOT_FOUND);
        }
        user.setRole(role);
        userRepository.save(user);
        return StatusDTO.builder().statusCode("S").msg("Role updated successfully!").build();
    }

    @Override
    public byte[] getCourseStudentExcelTemplate(){
        try{
            CourseStudentExcelWriter courseStudentExcelWriter = CourseStudentExcelWriter.builder()
                    .build();
            return courseStudentExcelWriter.create();
        }catch (Exception e){
            throw new MLMException(ExceptionCode.UNEXPECTED_ERROR);
        }
    }

    @Override
    public CourseDTO getCourseById(Long id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();

        if(Objects.isNull(id)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Course course = courseRepository.getById(id);
        if(Objects.isNull(course)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        if(jwtUser.getAuthorities().contains(new SimpleGrantedAuthority("lec"))){
             return course.toDTOForLecturer();
        }
        return course.toDTOForUsers();
    }
    @Override
    public CourseDTO getCourseByIdForLecturer(Long id){
        if(Objects.isNull(id)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Course course = courseRepository.getById(id);
        if(Objects.isNull(course)){
            throw new MLMException(ExceptionCode.COURSE_NOT_FOUND);
        }
        return course.toDTOForLecturer();
    }

    @Override
    public CourseDTOListResponse getCoursesForUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        List<CourseDTO> courseDTOList = courseRepository.getAllPublicCoursesAndRegisteredCourses(jwtUser.getId()).stream().map(Course::toDTOForUsers).collect(Collectors.toList());
        CourseDTOListResponse response = new CourseDTOListResponse();
        response.setCourseDTOList(courseDTOList);
        return response;
    }

    @Override
    public CourseDTOListResponse getCoursesForLecturer(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        List<CourseDTO> courseDTOList = courseRepository.getCoursesByLecturerId(jwtUser.getId()).stream().map(Course::toDTOForLecturer).collect(Collectors.toList());
        CourseDTOListResponse response = new CourseDTOListResponse();
        response.setCourseDTOList(courseDTOList);
        return response;
    }

    @Override
    public CourseMaterialDTO getCourseMaterialById(Long id){
        if(Objects.isNull(id)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        CourseMaterial courseMaterial = courseMaterialRepository.getById(id);
        if(Objects.isNull(courseMaterial)){
            throw new MLMException(ExceptionCode.MATERIAL_NOT_FOUND);
        }
        return courseMaterial.toFullContentDTO();
    }

}
