package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper=true)
public class UserDTO extends MLMBaseClassDTO {

    private Role role;
    private String fullName;
    private String username;
    private String password;


}
