package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.MyBooksDTO;
import lombok.Data;

import java.util.List;

@Data
public class MyBooksDTOListResponse extends  PageableResponse{
    private List<MyBooksDTO> myBooksDTOList;

}
