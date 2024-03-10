package com.metuncc.mlm.service;

import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.request.GetReceiptRequest;
import com.metuncc.mlm.api.response.*;
import com.metuncc.mlm.api.request.FindUserRequest;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface MlmQueryServices {

    UserDTO getOneUserByUserName(String username);

    ShelfDTO getShelfById(Long id);

    ShelfDTOListResponse getAllShelfs();

    ImageDTO getImageById(Long id);

    UserDTOListResponse getUsersBySpecifications(FindUserRequest request);

    BookDTO getBookById(Long id);

    BookDTOListResponse getBooksByShelfId(Long id);

    UserDTO getUserDetails();

    BookDTOListResponse getBooksBySpecification(FindBookRequest request);

    RoomDTO getRoomById(Long id);

    RoomDTOListResponse getRooms();

    CopyCardDTO getCopyCardDetails();

    ReceiptHistoryDTOListResponse getReceiptsOfUser();

    ReceiptHistoryDTOListResponse getReceipts();


    ReceiptHistoryDTOListResponse getReceiptsByStatus(GetReceiptRequest request);

    ReceiptHistoryDTOListResponse getReceiptsByUser(Long id);

    ReceiptHistoryDTOHashMapResponse getReceiptsHashMap();

    OpenLibraryBookDetails getBookDetailsFromExternalWithISBN(String isbn);

    List<BookCategoryEnumDTO> getAllBookCategories();


    StatisticsDTO getStatistics();

    UserNamesDTOListResponse getAllUsers();

    MyBooksDTOListResponse getMyBooks();

    StatusDTO getQueueStatusBasedOnBook(Long bookId);

    QueueDetailDTO getQueueStatusBasedOnBookForLibrarian(Long bookId);

    List<StatisticsDTO> getStatisticsForChart();

    List<BookReviewDTO> getBookReviewsByBookId(Long id);

    byte[] getExcel();
}
