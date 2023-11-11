package com.metuncc.mlm.entity;

import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.dto.ImageDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "image")
public class Image extends MLMBaseClass{

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] imageData;

    private String name;

    private String type;

    public ImageDTO toDTO(){
        ImageDTO dto =  ImageDTO
                .builder()
                .name(getName())
                .imageData(getImageData())
                .build();
        dto.setId(getId());
        return dto;
    }
}
