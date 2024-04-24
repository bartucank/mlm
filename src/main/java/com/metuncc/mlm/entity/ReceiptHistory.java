package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.ReceiptHistoryDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.ReceiptStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ReceiptHistory extends MLMBaseClass {
    @ManyToOne
    private User user;
    @OneToOne
    private Image img;
    @Enumerated(value = EnumType.STRING)
    private ReceiptStatus approved;
    private BigDecimal balance;

    public ReceiptHistoryDTO toDTO(){
        ReceiptHistoryDTO receiptHistoryDTO = new ReceiptHistoryDTO();
        receiptHistoryDTO.setId(getId());
        receiptHistoryDTO.setUserId(getUser().getId());
        receiptHistoryDTO.setImgId(getImg().getId());
        receiptHistoryDTO.setApprovedEnum(getApproved());
        receiptHistoryDTO.setBalance(getBalance());
        return receiptHistoryDTO;
    }


    public ReceiptHistoryDTO toForListDTO(){
        ReceiptHistoryDTO receiptHistoryDTO = new ReceiptHistoryDTO();
        receiptHistoryDTO.setId(getId());
        receiptHistoryDTO.setUserId(getUser().getId());
        receiptHistoryDTO.setImgId(getImg().getId());
        receiptHistoryDTO.setApproved(getApproved().equals(ReceiptStatus.APPROVED));
        receiptHistoryDTO.setApprovedEnum(getApproved());
        receiptHistoryDTO.setBalance(getBalance());
        return receiptHistoryDTO;
    }
}
