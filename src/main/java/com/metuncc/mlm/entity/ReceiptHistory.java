package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.ReceiptHistoryDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ReceiptHistory extends MLMBaseClass {
    @ManyToOne
    private User user;
    @OneToOne
    private Image img;
    private Boolean approved;
    private BigDecimal balance;

    public ReceiptHistoryDTO toDTO(){
        ReceiptHistoryDTO receiptHistoryDTO = new ReceiptHistoryDTO();
        receiptHistoryDTO.setUserId(getUser().getId());
        receiptHistoryDTO.setImgId(getImg().getId());
        receiptHistoryDTO.setApproved(getApproved());
        receiptHistoryDTO.setBalance(getBalance());
        return receiptHistoryDTO;
    }
}