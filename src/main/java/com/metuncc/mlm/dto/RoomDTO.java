package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.Image;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RoomDTO extends MLMBaseClassDTO{
    private String name;
    private Long imageId;
    private String NFC_no;;
    private Long qrImage;
}
