package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.api.response.*;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.dto.ImageDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value ="/api/user", produces = "application/json;charset=UTF-8")
public class MLMController {
    private MlmServices mlmServices;
    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    public MLMController( MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService) {
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
    }


    @GetMapping("/shelf/getById")
    public ResponseEntity<ShelfDTO> getShelf(@RequestParam Long shelfId){
        return new ResponseEntity<>(mlmQueryServices.getShelfById(shelfId), HttpStatus.OK);
    }
    @GetMapping("/shelf/getAll")
    public ResponseEntity<ApiResponse<ShelfDTOListResponse>> getAllShelfs(){
        return responseService.createResponse(mlmQueryServices.getAllShelfs());
    }

    @GetMapping("/getImageById")
    public ResponseEntity<ApiResponse<ImageDTO>>  getImageByName(@RequestParam(name = "id")Long id){
        return responseService.createResponse(mlmQueryServices.getImageById(id));
    }
    @GetMapping("/getImageBase64ById")
    public byte[]  getImageBase64ById(@RequestParam(name = "id")Long id){
        return getImageByName(id).getBody().getData().getImageData();
    }

    @GetMapping("/getUserDetails")
    public ResponseEntity<ApiResponse<UserDTO>> getUserDetails(){
        return responseService.createResponse(mlmQueryServices.getUserDetails());
    }

    @GetMapping("/book/getBookById")
    public ResponseEntity<ApiResponse<BookDTO>>  getBookById(@RequestParam(name = "id")Long id){
        return responseService.createResponse(mlmQueryServices.getBookById(id));
    }
    @GetMapping("/book/getBooksByShelfId")
    public ResponseEntity<ApiResponse<BookDTOListResponse>>  getBooksByShelfId(@RequestParam(name = "shelfId")Long shelfId){
        return responseService.createResponse(mlmQueryServices.getBooksByShelfId(shelfId));
    }
    @PostMapping("/book/getBooksBySpecification")
    public ResponseEntity<ApiResponse<BookDTOListResponse>> getBooksBySpecification(@RequestBody FindBookRequest request){
        return responseService.createResponse(mlmQueryServices.getBooksBySpecification(request));
    }
    @PostMapping("/makeReservation")
    public ResponseEntity<ApiResponse<StatusDTO>> makeReservation(@RequestParam(name = "roomSlotId")Long roomSlotId){
        return responseService.createResponse(mlmServices.makeReservation(roomSlotId));
    }
    @PostMapping("/cancelReservation")
    public ResponseEntity<ApiResponse<StatusDTO>> cancelReservation(@RequestParam(name = "roomReservationId")Long roomReservationId){
        return responseService.createResponse(mlmServices.cancelReservation(roomReservationId));
    }
    @GetMapping("/getReceiptsofUser")
    public ResponseEntity<ApiResponse<ReceiptHistoryDTOListResponse>> getReceiptsOfUser(){
        return responseService.createResponse(mlmQueryServices.getReceiptsOfUser());
    }
    @PostMapping("/createReceipt")
    public ResponseEntity<ApiResponse<StatusDTO>> createReceipt(@RequestParam (name = "imageId") Long imageId){
        return responseService.createResponse((mlmServices.createReceiptHistory(imageId)));
    }
    @PostMapping(value="/uploadImage",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StatusDTO>> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        return responseService.createResponse(mlmServices.uploadImage(file));
    }

    @GetMapping("/book/getAllCategories")
    public ResponseEntity<ApiResponse<BookCategoryEnumDTOListResponse>>  getAllBookCategories(){
        BookCategoryEnumDTOListResponse response = new BookCategoryEnumDTOListResponse();
        response.setList(mlmQueryServices.getAllBookCategories());
        return responseService.createResponse(response);
    }

    @GetMapping("/myBooks")
    public ResponseEntity<ApiResponse<MyBooksDTOListResponse>> getMyBooks(){
        return responseService.createResponse(mlmQueryServices.getMyBooks());
    }
    @GetMapping(value="/getQueueStatusBasedOnBook")
    public ResponseEntity<ApiResponse<StatusDTO>> getQueueStatusBasedOnBook(@RequestParam("id") Long id) {
        return responseService.createResponse(mlmQueryServices.getQueueStatusBasedOnBook(id));
    }
    @PostMapping(value="/enqueue")
    public ResponseEntity<ApiResponse<StatusDTO>> enqueue(@RequestParam("id") Long id) {
        return responseService.createResponse(mlmServices.enqueue(id));
    }
    @PostMapping(value="/addReview")
    public ResponseEntity<ApiResponse<StatusDTO>> addReview(@RequestBody AddReviewRequest request) {
        return responseService.createResponse(mlmServices.addReview(request));
    }

    @GetMapping(value="/getRoomById")
    public ResponseEntity<ApiResponse<RoomDTO>> getRoomById(@RequestParam("id") Long id) {
        return responseService.createResponse(mlmQueryServices.getRoomById(id));
    }
    @GetMapping(value="/getRooms")
    public ResponseEntity<ApiResponse<RoomDTOListResponse>> getRooms() {
        return responseService.createResponse(mlmQueryServices.getRooms());
    }
    @GetMapping(value="/getRoomSlotsByRoomId")
    public ResponseEntity<ApiResponse<RoomSlotDTOListResponse>> getRoomSlotsById(@RequestParam("id") Long id) {
        return responseService.createResponse(mlmQueryServices.getRoomSlotsById(id));
    }
    @PostMapping(value = "/changePassword")
    public ResponseEntity<ApiResponse<LoginResponse>> changePassword(@RequestBody ChangePasswordRequest request){
        return responseService.createResponse(mlmServices.changePassword(request));
    }
    @GetMapping("/course/getCourseById")
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseById(@RequestParam("id") Long id){
        return responseService.createResponse(mlmQueryServices.getCourseById(id));
    }
    @GetMapping("/course/getCoursesForUser")
    public ResponseEntity<ApiResponse<CourseDTOListResponse>> getCoursesForUser(){
        return responseService.createResponse(mlmQueryServices.getCoursesForUser());
    }
    @GetMapping("/course/getCourseMaterialById")
    public ResponseEntity<ApiResponse<CourseMaterialDTO>> getCourseMaterialById(@RequestParam("id") Long id){
        return responseService.createResponse(mlmQueryServices.getCourseMaterialById(id));
    }
    //getBookReviewsByBookId
    @GetMapping("/book/getBookReviewsByBookId")
    public ResponseEntity<ApiResponse<List<BookReviewDTO>>> getBookReviewsByBookId(@RequestParam("id") Long id){
        return responseService.createResponse(mlmQueryServices.getBookReviewsByBookId(id));
    }

    @PostMapping("/favorite/addToFavorite")
    public ResponseEntity<ApiResponse<StatusDTO>> addToFavorite(@RequestParam("bookId") Long bookId){
        return responseService.createResponse(mlmServices.addToFavorite(bookId));
    }

    @GetMapping("/favorite/getFavorites")
    public ResponseEntity<ApiResponse<BookDTOListResponse>> getFavorites(){
        return responseService.createResponse(mlmQueryServices.getFavorites());
    }

    @GetMapping("/ebook/getEbook")
    public ResponseEntity<ApiResponse<EbookDTO>> getEbook(@RequestParam("id") Long id){
        return responseService.createResponse(mlmQueryServices.getEbook(id));
    }

    @DeleteMapping("/favorite/removeFavorite")
    public ResponseEntity<ApiResponse<StatusDTO>> removeFavorite(@RequestParam("bookId") Long bookId){
        return responseService.createResponse(mlmServices.removeFavorite(bookId));
    }

    @GetMapping("/favorite/isFavorited")
    public ResponseEntity<ApiResponse<Boolean>> isFavorited(@RequestParam("bookId") Long bookId){
        return responseService.createResponse(mlmQueryServices.isFavorited(bookId));
    }
}
