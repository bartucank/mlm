package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.UserNamesDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserNamesDTOListResponse {
    private List<UserNamesDTO> dtoList;
}
