package com.metuncc.mlm.api.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String jwt;
    private Boolean needVerify;
}
