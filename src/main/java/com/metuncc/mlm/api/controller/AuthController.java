package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.StatusDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(value ="/api/auth", produces = "application/json;charset=UTF-8")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private MlmServices mlmServices;

    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserRequest userRequest){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userRequest.getUsername(),
                        userRequest.getPass());
        Authentication authentication = authenticationManager
                .authenticate(usernamePasswordAuthenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token =  jwtTokenProvider.generateJwtToken(authentication);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setJwt(token);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<StatusDTO>> register(@RequestBody UserRequest userRequest){
        return responseService.createResponse(mlmServices.createUser(userRequest));
    }
}
