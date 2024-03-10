package com.metuncc.mlm.api.request;

import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UploadImageByBase64 {
   private String base64;
}
