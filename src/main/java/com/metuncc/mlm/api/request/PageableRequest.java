package com.metuncc.mlm.api.request;

import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PageableRequest {

    private Integer page;
    private Integer size;

}
