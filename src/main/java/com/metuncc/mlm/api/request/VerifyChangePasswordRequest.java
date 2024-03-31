package com.metuncc.mlm.api.request;

import lombok.Data;

@Data
public class VerifyChangePasswordRequest {
    private String code;
    private String newPassword;
}
