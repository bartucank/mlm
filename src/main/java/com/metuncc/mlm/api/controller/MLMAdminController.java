package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.api.response.ReceiptHistoryDTOHashMapResponse;
import com.metuncc.mlm.api.response.ReceiptHistoryDTOListResponse;
import com.metuncc.mlm.api.response.UserDTOListResponse;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value ="/api/admin", produces = "application/json;charset=UTF-8")
public class MLMAdminController {
    private MlmServices mlmServices;
    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    public MLMAdminController(MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService) {
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
    }

    @PostMapping("/shelf/create")
    public ResponseEntity<ApiResponse<StatusDTO>> createShelf(@RequestBody ShelfCreateRequest shelfCreateRequest){
        return responseService.createResponse(mlmServices.createShelf(shelfCreateRequest));
    }

    @PutMapping("/shelf/update")
    public ResponseEntity<ApiResponse<StatusDTO>> updateShelf(@RequestBody ShelfCreateRequest shelfCreateRequest){
        return responseService.createResponse(mlmServices.updateShelf(shelfCreateRequest));
    }
    @PostMapping(value="/uploadImage",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StatusDTO>> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        return responseService.createResponse(mlmServices.uploadImage(file));
    }

    @PostMapping("/user/getUsersBySpecifications")
    public ResponseEntity<ApiResponse<UserDTOListResponse>> getUsersBySpecifications(@RequestBody FindUserRequest request){
        return responseService.createResponse(mlmQueryServices.getUsersBySpecifications(request));
    }

    @PostMapping("/book/create")
    public ResponseEntity<ApiResponse<StatusDTO>> createBook(@RequestBody BookRequest request){
        return responseService.createResponse(mlmServices.createBook(request));
    }

    @PutMapping("/book/update")
    public ResponseEntity<ApiResponse<StatusDTO>> updateBook(@RequestBody BookRequest request){
        return responseService.createResponse(mlmServices.updateBook(request));
    }
    @PutMapping("/book/delete")
    public ResponseEntity<ApiResponse<StatusDTO>> deleteBook(@RequestParam("id") Long id){
        return responseService.createResponse(mlmServices.deleteBook(id));
    }

    @PostMapping("/book/borrow")
    public ResponseEntity<ApiResponse<StatusDTO>> borrowBook(@RequestParam("bookId") Long bookId,
                                                             @RequestParam("userId") Long userId){
        return responseService.createResponse(mlmServices.borrowBook(bookId,userId));
    }

    @PostMapping("/book/takeBackBook")
    public ResponseEntity<ApiResponse<StatusDTO>> takeBackBook(@RequestParam("bookId") Long bookId,
                                                             @RequestParam("userId") Long userId){
        return responseService.createResponse(mlmServices.takeBackBook(bookId,userId));
    }
    @PostMapping("/user/generateQRcodeForRoom")
    public ResponseEntity<ApiResponse<StatusDTO>> generateQRcodeForRoom(@RequestParam("roomId") Long roomId){
        return responseService.createResponse(mlmServices.generateQRcodeForRoom(roomId));
    }
    @PostMapping("/user/readingNFC")
    public ResponseEntity<ApiResponse<StatusDTO>> readingNFC(@RequestParam("NFC_no") String NFC_no,
                                                             @RequestParam("roomId") Long roomId){
        return responseService.createResponse(mlmServices.readingNFC(NFC_no,roomId));
    }
    @PostMapping("/user/createReceipt")
    public ResponseEntity<ApiResponse<StatusDTO>> createReceipt(@RequestBody ReceiptRequest request){
        return responseService.createResponse((mlmServices.createReceiptHistory(request)));
    }
    @PostMapping("/user/getReceipts")
    public ResponseEntity<ApiResponse<ReceiptHistoryDTOListResponse>> getReceipts(){
        return responseService.createResponse((mlmQueryServices.getReceipts()));
    }
    @GetMapping("/user/getReceiptByUser")
    public ResponseEntity<ApiResponse<ReceiptHistoryDTOListResponse>> getReceiptsByUser(@RequestParam("userId") Long userId){
        return responseService.createResponse(mlmQueryServices.getReceiptsByUser(userId));
    }
    @GetMapping("/user/getReceiptsHashMap")
    public ResponseEntity<ApiResponse<ReceiptHistoryDTOHashMapResponse>> getReceiptsHashMap(){
        return responseService.createResponse(mlmQueryServices.getReceiptsHashMap());
    }

}
