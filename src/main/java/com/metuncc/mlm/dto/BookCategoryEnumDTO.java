package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
public class BookCategoryEnumDTO {
    private String str;
    private String enumValue;

}
