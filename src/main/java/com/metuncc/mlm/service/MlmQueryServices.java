package com.metuncc.mlm.service;

import com.metuncc.mlm.api.request.FindUserRequest;
import com.metuncc.mlm.api.response.ShelfDTOListResponse;
import com.metuncc.mlm.api.response.UserDTOListResponse;
import com.metuncc.mlm.dto.ImageDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.User;

import java.util.List;

public interface MlmQueryServices {

    UserDTO getOneUserByUserName(String username);

    ShelfDTO getShelfById(Long id);

    ShelfDTOListResponse getAllShelfs();

    ImageDTO getImageById(Long id);

    UserDTOListResponse getUsersBySpecifications(FindUserRequest request);
}
