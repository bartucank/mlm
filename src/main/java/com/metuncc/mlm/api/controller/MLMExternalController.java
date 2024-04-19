package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.AddReviewRequest;
import com.metuncc.mlm.api.request.ChangePasswordRequest;
import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.response.*;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.dto.ml.LightBook;
import com.metuncc.mlm.dto.ml.LightReview;
import com.metuncc.mlm.dto.ml.LightUser;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value ="/api/external", produces = "application/json;charset=UTF-8")
public class MLMExternalController {
    private MlmServices mlmServices;
    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    public MLMExternalController(MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService) {
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
    }

    @GetMapping("/getLightBooks")
    public ResponseEntity<ApiResponse<List<LightBook>>> getLightBooks(){
        return responseService.createResponse(mlmQueryServices.getLightBooks());
    }

    @GetMapping("/getLightUsers")
    public ResponseEntity<ApiResponse<List<LightUser>>> getLightUsers(){
        return responseService.createResponse(mlmQueryServices.getLightUsers());
    }

    @GetMapping("/getLightReviews")
    public ResponseEntity<ApiResponse<List<LightReview>>> getLightReviews(){
        return responseService.createResponse(mlmQueryServices.getLightReviews());
    }
}
