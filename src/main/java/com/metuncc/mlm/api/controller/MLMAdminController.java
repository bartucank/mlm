package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.api.response.*;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.OpenLibraryBookDetails;
import com.metuncc.mlm.dto.QueueDetailDTO;
import com.metuncc.mlm.dto.StatisticsDTO;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

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
    public ResponseEntity<ApiResponse<StatusDTO>> takeBackBook(@RequestParam("bookId") Long bookId){
        return responseService.createResponse(mlmServices.takeBackBook(bookId));
    }
    @PostMapping("/generateQRcodeForRoom")
    public ResponseEntity<ApiResponse<StatusDTO>> generateQRcodeForRoom(@RequestParam("roomId") Long roomId){
        return responseService.createResponse(mlmServices.generateQRcodeForRoom(roomId));
    }
    @PostMapping("/readingNFC")
    public ResponseEntity<ApiResponse<StatusDTO>> readingNFC(@RequestParam("NFC_no") String NFC_no,
                                                             @RequestParam("roomId") Long roomId){
        return responseService.createResponse(mlmServices.readingNFC(NFC_no,roomId));
    }
    @GetMapping("/book/getByISBN")
    public ResponseEntity<ApiResponse<OpenLibraryBookDetails>> getBookDetailsFromExternalWithISBN(@RequestParam("isbn")String isbn){
        return responseService.createResponse(mlmQueryServices.getBookDetailsFromExternalWithISBN(isbn));
    }


    @GetMapping("/getReceipts")
    public ResponseEntity<ApiResponse<ReceiptHistoryDTOListResponse>> getReceipts(){
        return responseService.createResponse((mlmQueryServices.getReceipts()));
    }
    @PostMapping("/getReceiptsByStatus")
    public ResponseEntity<ApiResponse<ReceiptHistoryDTOListResponse>> getReceiptsByStat(@RequestBody GetReceiptRequest request){
        return responseService.createResponse((mlmQueryServices.getReceiptsByStatus(request)));
    }
    @GetMapping("/getReceiptByUser")
    public ResponseEntity<ApiResponse<ReceiptHistoryDTOListResponse>> getReceiptsByUser(@RequestParam("userId") Long id){
        return responseService.createResponse(mlmQueryServices.getReceiptsByUser(id));
    }
    @GetMapping("/getReceiptsHashMap")
    public ResponseEntity<ApiResponse<ReceiptHistoryDTOHashMapResponse>> getReceiptsHashMap(){
        return responseService.createResponse(mlmQueryServices.getReceiptsHashMap());
    }
    @PutMapping("/approveReceipt")
    public ResponseEntity<ApiResponse<StatusDTO>> approveReceipt(@RequestParam("receiptId") Long id,
                                                                 @RequestParam("balance")BigDecimal balance){
        return responseService.createResponse(mlmServices.approveReceipt(id, balance));
    }
    @GetMapping("/getStatistics")
    public ResponseEntity<ApiResponse<StatisticsDTO>> getStatistics(){
        return responseService.createResponse(mlmQueryServices.getStatistics());
    }
    @GetMapping("/getUsersForBorrowPage")
    public ResponseEntity<ApiResponse<UserNamesDTOListResponse>> getUsersForBorrowPage(){
        return responseService.createResponse(mlmQueryServices.getAllUsers());
    }

    @GetMapping(value="/getQueueStatusBasedOnBookForLibrarian")
    public ResponseEntity<ApiResponse<QueueDetailDTO>> getQueueStatusBasedOnBookForLibrarian(@RequestParam("id") Long id) {
        return responseService.createResponse(mlmQueryServices.getQueueStatusBasedOnBookForLibrarian(id));
    }
    @GetMapping("/getStatisticsForChart")
    public ResponseEntity<ApiResponse<StatisticsDTOListResponse>> getStatisticsForChart(){
        StatisticsDTOListResponse response = new StatisticsDTOListResponse();
        response.setStatisticsDTOList(mlmQueryServices.getStatisticsForChart());
        return responseService.createResponse(response);
    }

    @GetMapping("/getExcel")
    public ResponseEntity<Resource> getExcel(){
        byte[] bytes = mlmQueryServices.getExcel();
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=Excel.xlsx");
        return ResponseEntity.ok().headers(headers).contentLength(bytes.length).contentType(MediaType.parseMediaType("application/ms-excel; charset=UTF-8")).body(byteArrayResource);
    }

    @PostMapping("/uploadExcel")
    public ResponseEntity<ApiResponse<StatusDTO>> uploadExcel(@RequestPart("file")MultipartFile file){
        return responseService.createResponse(mlmServices.bulkCreateBook(file));
    }
}
