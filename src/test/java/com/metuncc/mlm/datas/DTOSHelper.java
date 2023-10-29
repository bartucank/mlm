package com.metuncc.mlm.datas;

import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.Role;

import java.time.LocalDateTime;

public class DTOSHelper {

    public UserDTO userDTO(){
        User user = new User();
        user.setPassword("1234");
        user.setRole(Role.USER);
        user.setUsername("username");
        user.setFullName("full name");
        user.setId(1L);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setDeleted(false);
        user.setDeletedDate(null);
        return user.toDTO();
    }
}
