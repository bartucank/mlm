package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.api.response.BookDTOListResponse;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.api.response.ShelfDTOListResponse;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.dto.ImageDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.service.MlmBorrowServices;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value ="/api/user", produces = "application/json;charset=UTF-8")
public class MLMController {
    private MlmServices mlmServices;
    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    private MlmBorrowServices mlmBorrowServices;
    public MLMController( MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService,
    MlmBorrowServices mlmBorrowServices) {
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
        this.mlmBorrowServices = mlmBorrowServices;
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
    @GetMapping("book/getBooksBySpecification")
    public ResponseEntity<ApiResponse<BookDTOListResponse>> getBooksBySpecification(@RequestBody FindBookRequest request){
        return responseService.createResponse(mlmQueryServices.getBooksBySpecification(request));
    }

}
