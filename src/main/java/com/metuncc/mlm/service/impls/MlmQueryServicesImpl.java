package com.metuncc.mlm.service.impls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.response.*;
import com.metuncc.mlm.api.request.FindUserRequest;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import com.metuncc.mlm.entity.enums.QueueStatus;
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
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import java.util.Objects;
import java.util.stream.Collectors;

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
        return dto;
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

        return statisticsDTO;
    }
}
