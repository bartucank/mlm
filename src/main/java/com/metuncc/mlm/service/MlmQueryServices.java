package com.metuncc.mlm.service;

import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.response.BookDTOListResponse;
import com.metuncc.mlm.api.request.FindUserRequest;
import com.metuncc.mlm.api.response.RoomDTOListResponse;
import com.metuncc.mlm.api.response.ShelfDTOListResponse;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.api.response.UserDTOListResponse;
import com.metuncc.mlm.entity.User;

import java.util.List;

public interface MlmQueryServices {

    UserDTO getOneUserByUserName(String username);

    ShelfDTO getShelfById(Long id);

    ShelfDTOListResponse getAllShelfs();

    ImageDTO getImageById(Long id);

    UserDTOListResponse getUsersBySpecifications(FindUserRequest request);

    BookDTO getBookById(Long id);

    BookDTOListResponse getBooksByShelfId(Long id);

    UserDTO getUserDetails();

    BookDTOListResponse getBooksBySpecification(FindBookRequest request);

    RoomDTO getRoomById(Long id);

    RoomDTOListResponse getRooms();
}
