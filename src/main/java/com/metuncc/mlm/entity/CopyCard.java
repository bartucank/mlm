package com.metuncc.mlm.entity;

import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.dto.CopyCardDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class CopyCard extends MLMBaseClass {

    @OneToOne
    private User owner;
    private String nfcCode;
    private BigDecimal balance;


    public CopyCardDTO toDTO(){
        CopyCardDTO dto = new CopyCardDTO();
        dto.setBalance(getBalance());
        dto.setNfcCode(getNfcCode());
        dto.setId(getId());
        dto.setOwnerId(getOwner().getId());
        return dto;
    }
}
