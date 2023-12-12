package com.metuncc.mlm.dto;


import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper=true)
public class ReceiptHistoryDTO extends MLMBaseClassDTO {

    private Long userId;
    private Long imgId;
    private Boolean approved;
    private BigDecimal balance;


}
