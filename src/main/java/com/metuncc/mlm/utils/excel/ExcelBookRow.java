package com.metuncc.mlm.utils.excel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExcelBookRow {
    private String isbn;
    private String bookName;
    private String desc;
    private String publisher;
    private String author;
    private String shelf;
    private String category;
}
