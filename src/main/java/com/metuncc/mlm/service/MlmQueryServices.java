package com.metuncc.mlm.service;

import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.User;

public interface MlmQueryServices {

    UserDTO getOneUserByUserName(String username);
}
