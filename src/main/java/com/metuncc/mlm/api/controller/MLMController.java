package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.response.BookDTOListResponse;
import com.metuncc.mlm.api.response.ReceiptHistoryDTOListResponse;
import com.metuncc.mlm.api.response.ShelfDTOListResponse;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.dto.ImageDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<ShelfDTO>> getShelf(@RequestParam Long shelfId){
        return responseService.createResponse(mlmQueryServices.getShelfById(shelfId));
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
}
