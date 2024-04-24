package com.metuncc.mlm.dto;


import com.metuncc.mlm.entity.enums.ReceiptStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper=true)
public class ReceiptHistoryDTO extends MLMBaseClassDTO {

    private Long userId;
    private Long imgId;
    private ReceiptStatus approved;
    private BigDecimal balance;


}
