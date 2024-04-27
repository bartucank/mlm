package com.metuncc.mlm.api.request;

import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class DetailedFilter {

    private List<Long> shelfId; //long list
    private List<String> publisher; // like
    private String name; //like
    private List<String> author;//like
    private List<BookCategory> category; //equals
    private List<BookStatus> status; //equals
    private Boolean ebook;  //true false gelsin null or not null
}
