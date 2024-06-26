package com.metuncc.mlm.service;

import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.enums.RoomSlotDays;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;

public interface MlmServices {
    LoginResponse createUser(UserRequest userRequest);

    LoginResponse login(UserRequest userRequest);

    StatusDTO createShelf(ShelfCreateRequest request);

    StatusDTO updateShelf(ShelfCreateRequest request);

    StatusDTO uploadImage(MultipartFile file) throws IOException;

    StatusDTO uploadImageByBase64(UploadImageByBase64 request) throws IOException;

    Image uploadImageReturnImage(MultipartFile file) throws IOException;

    StatusDTO verifyEmail(String code);

    StatusDTO createBook(BookRequest request);

    StatusDTO updateBook(BookRequest request);

    StatusDTO deleteBook(Long id);

    StatusDTO createRoom(CreateRoomRequest request);

    StatusDTO setNFCForRoom(Long roomId, String nfcNo);

    StatusDTO deleteRoom(Long id);


    StatusDTO createSlots(RoomSlotDays day, String start, String end);

    StatusDTO enqueue(Long bookId);

    StatusDTO borrowBook(Long bookId, Long userId);



    StatusDTO takeBackBook(Long bookId);

    StatusDTO givePhysicalCopyCardToUser(String nfcCode, Long userId);

    Boolean checkNowReservationExists();


    Boolean approveReservation(String secret);

    StatusDTO makeReservation(Long roomSlotId);

    StatusDTO cancelReservation(Long roomReservationId);

    StatusDTO generateQRcodeForRoom(Long roomId);

    StatusDTO readingNFC(String NFC_no, Long roomId);

    StatusDTO approveReservation(String nfcCode, String qrCode);

    StatusDTO createReceiptHistory(Long imageId);

    StatusDTO approveReceipt(Long id, BigDecimal balance);

    StatusDTO addReview(AddReviewRequest request);

    StatusDTO bulkCreateBook(MultipartFile file);

    LoginResponse changePassword(ChangePasswordRequest request);

    StatusDTO startForgotPasswordProcess(UserRequest request);

    Boolean checkCodeForResetPassword(String code);

    Boolean completeCodeForResetPassword(VerifyChangePasswordRequest request);

    StatusDTO createCourse(CreateCourseRequest createCourseRequest);

    StatusDTO inviteStudent(InviteStudentRequest request);

    StatusDTO bulkAddStudentToCourse(MultipartFile file, Long courseId);

    StatusDTO uploadCourseMaterial(MultipartFile file, Long courseId, String name) throws IOException;

    StatusDTO deleteCourseMaterial(Long materialId);

    @Transactional
    StatusDTO deleteCourse(Long courseId);

    StatusDTO removeStudentFromCourse(Long courseId, Long courseStudentId);

    StatusDTO finishCourseTerm(Long courseId);

    StatusDTO bulkRemoveStudentFromCourse(Long courseId, MultipartFile file);

    StatusDTO addToFavorite(Long bookId);

    StatusDTO addEbook(Long bookId, MultipartFile file) throws IOException;

    StatusDTO deleteEBook(Long bookId);

    StatusDTO rejectReceipt(Long receiptId);

    StatusDTO removeFavorite(Long bookId);

    StatusDTO moveShelf(Long oldShelfId, Long newShelfId);

    StatusDTO deleteShelf(Long oldShelfId, Long newShelfId);
}
