package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.UserDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserDTOListResponse {
    private List<UserDTO> userDTOList;
}
