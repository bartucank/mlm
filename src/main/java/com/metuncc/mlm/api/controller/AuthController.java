package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.api.request.VerifyChangePasswordRequest;
import com.metuncc.mlm.api.response.DepartmentDTOListResponse;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(value ="/api/auth", produces = "application/json;charset=UTF-8")
public class AuthController {

    private MlmServices mlmServices;

    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    public AuthController(MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService) {
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody UserRequest userRequest){
        return responseService.createResponse(mlmServices.login(userRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody UserRequest userRequest){
        return responseService.createResponse(mlmServices.createUser(userRequest));
    }
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<StatusDTO>> verify(@RequestParam String code){
        return responseService.createResponse(mlmServices.verifyEmail(code));
    }
    @PostMapping("/startForgotPasswordProcess")
    public ResponseEntity<ApiResponse<StatusDTO>> startForgotPasswordProcess(@RequestBody UserRequest request){
        return responseService.createResponse(mlmServices.startForgotPasswordProcess(request));
    }

    @PostMapping("/checkCodeForResetPassword")
    public ResponseEntity<ApiResponse<Boolean>> checkCodeForResetPassword(@RequestParam String code){
        return responseService.createResponse(mlmServices.checkCodeForResetPassword(code));
    }
    @PostMapping("/completeCodeForResetPassword")
    public ResponseEntity<ApiResponse<Boolean>> completeCodeForResetPassword(@RequestBody VerifyChangePasswordRequest request){
        return responseService.createResponse(mlmServices.completeCodeForResetPassword(request));
    }

    @GetMapping("/getDeps")
    public ResponseEntity<ApiResponse<DepartmentDTOListResponse>> getDeps(){
        return responseService.createResponse(mlmQueryServices.getDeps());
    }

    @GetMapping("/getDepsssss")
    public ResponseEntity<ApiResponse<DepartmentDTOListResponse>> getDepsss(){
        return responseService.createResponse(mlmQueryServices.getDeps());
    }

}
