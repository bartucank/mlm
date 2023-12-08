package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.UserDTO;
import lombok.Data;

import java.util.List;

@Data
public class PageableResponse {
    private int page;
    private int size;
    private long totalResult;
    private int totalPage;
}
