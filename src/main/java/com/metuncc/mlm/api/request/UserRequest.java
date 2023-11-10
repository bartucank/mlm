package com.metuncc.mlm.api.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserRequest {
    private String username;
    private String pass;
    private String nameSurname;

    private String email;
}
