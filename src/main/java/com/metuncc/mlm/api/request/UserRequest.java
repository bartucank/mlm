package com.metuncc.mlm.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String username;
    private String pass;
    private String nameSurname;
}
