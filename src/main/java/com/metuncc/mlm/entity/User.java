package com.metuncc.mlm.entity;

import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sysUser")
public class User extends MLMBaseClass {

    @Enumerated(value = EnumType.STRING)
    private Role role;
    private String fullName;
    private String username;
    private String password;


    public UserDTO toDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(getId());
        userDTO.setRole(getRole());
        userDTO.setUsername(getUsername());
        userDTO.setFullName(getFullName());
        return userDTO;
    }

    public User fromRequest(UserRequest userRequest) {
        setUsername(userRequest.getUsername());
        setFullName(userRequest.getNameSurname());
        setRole(Role.USER);
        return this;
    }
}
