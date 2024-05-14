package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ShelfDTO extends MLMBaseClassDTO {

    private String floor;
    private Long bookCount;


}
