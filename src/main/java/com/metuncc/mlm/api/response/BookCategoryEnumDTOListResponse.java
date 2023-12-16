package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.BookCategoryEnumDTO;
import lombok.Data;

import java.util.List;

@Data
public class BookCategoryEnumDTOListResponse {
    private List<BookCategoryEnumDTO> list;
}
