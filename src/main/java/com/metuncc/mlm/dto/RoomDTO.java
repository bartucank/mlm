package com.metuncc.mlm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RoomDTO extends MLMBaseClassDTO{
    private String name;
    private Long imageId;
}
