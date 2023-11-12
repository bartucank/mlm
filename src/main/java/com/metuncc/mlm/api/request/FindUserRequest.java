package com.metuncc.mlm.api.request;

import com.metuncc.mlm.entity.enums.Role;
import lombok.Data;

@Data
public class FindUserRequest {
    private Role role;
    private String fullName;
    private String username;
    private Boolean verified;
    private String email;

}
