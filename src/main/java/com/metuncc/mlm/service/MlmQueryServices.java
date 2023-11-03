package com.metuncc.mlm.service;

import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.User;

import java.util.List;

public interface MlmQueryServices {

    UserDTO getOneUserByUserName(String username);

    ShelfDTO getShelfById(Long id);

    List<ShelfDTO> getAllShelfs();
}
