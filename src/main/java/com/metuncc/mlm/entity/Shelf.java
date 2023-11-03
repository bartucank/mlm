package com.metuncc.mlm.entity;

import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "shelf")
public class Shelf extends MLMBaseClass {

    private String floor;

    public ShelfDTO toDTO() {
        ShelfDTO shelfDTO = new ShelfDTO();
        shelfDTO.setId(getId());
        shelfDTO.setFloor(getFloor());
        return shelfDTO;
    }

    public Shelf fromRequest(ShelfCreateRequest request) {
        setFloor(request.getFloor());
        return this;
    }
}
