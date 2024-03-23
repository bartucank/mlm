package com.metuncc.mlm.entity;

import com.metuncc.mlm.api.request.CreateRoomRequest;
import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.dto.RoomDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Room extends MLMBaseClass{

    private String name;
    private String NFC_no;
    private String verfCode;
    @OneToOne
    private Image qrImage;
    @OneToOne
    private Image imageId;



    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomSlot> roomSlotList;


    public RoomDTO toDTO(){
        RoomDTO dto = new RoomDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setImageId(getImageId().getId());
        dto.setQrImage(getQrImage().getId());
        if(Objects.isNull(getNFC_no())){
            dto.setNFC_no("PLEASE SCAN NFC CARD");
        }
        else{
            dto.setNFC_no(getNFC_no());
        }
        return dto;
    }
}
