package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.BookDTO;
import lombok.Data;

import java.util.List;

@Data
public class BookDTOListResponse {
    private List<BookDTO> bookDTOList;
}
