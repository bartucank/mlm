package com.metuncc.mlm.service;

import com.metuncc.mlm.api.request.BookRequest;
import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MlmServices {
    LoginResponse createUser(UserRequest userRequest);

    LoginResponse login(UserRequest userRequest);

    StatusDTO createShelf(ShelfCreateRequest request);

    StatusDTO updateShelf(ShelfCreateRequest request);

    StatusDTO uploadImage(MultipartFile file) throws IOException;

    StatusDTO verifyEmail(String code);

    StatusDTO createBook(BookRequest request);

    StatusDTO updateBook(BookRequest request);

    StatusDTO deleteBook(Long id);
}
