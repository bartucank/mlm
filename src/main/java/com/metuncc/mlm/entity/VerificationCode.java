package com.metuncc.mlm.entity;

import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.VerificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class VerificationCode extends MLMBaseClass{

    private String code;
    private Boolean isCompleted;
    @OneToOne
    private User user;
    @Enumerated(value = EnumType.STRING)
    private VerificationType verificationType;

}
