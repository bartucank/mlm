package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper=true)
public class CopyCardDTO extends MLMBaseClassDTO {

    private Long ownerId;
    private BigDecimal balance;
    private String nfcCode;


}
