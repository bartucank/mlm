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
        return dto;
    }
}
