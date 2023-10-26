package com.metuncc.mlm.api.service;

import com.metuncc.mlm.dto.StatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    public <T> ResponseEntity<ApiResponse<T>> createResponse(T data) {
        if (data instanceof StatusDTO) {
            StatusDTO statusDTO = (StatusDTO) data;
            if ("S".equals(statusDTO.getStatusCode())) {
                return new ResponseEntity<>(new ApiResponse<>("Success", data), HttpStatus.OK);
            } else if ("E".equals(statusDTO.getStatusCode())) {
                return new ResponseEntity<>(new ApiResponse<>("Error", data), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ApiResponse<>("Success", data), HttpStatus.OK);
    }
}