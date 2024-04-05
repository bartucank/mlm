package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.Department;
import com.metuncc.mlm.entity.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper=true)
public class UserDTO extends MLMBaseClassDTO {

    private Role role;
    private String roleStr;
    private String fullName;
    private String username;
    private String password;
    private Boolean verified;
    private String email;
    private BigDecimal debt;
    private String studentNumber;
    private CopyCardDTO copyCardDTO;
    private Department department;
    private String departmentStr;

}
