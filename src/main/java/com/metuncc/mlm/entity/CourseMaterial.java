package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.CourseMaterialDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.utils.ImageUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class CourseMaterial extends MLMBaseClass {

    private String name;

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] data;

    private String fileName;
    private String extension;


    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public CourseMaterialDTO toDTO() {
        CourseMaterialDTO courseMaterialDTO = new CourseMaterialDTO();
        courseMaterialDTO.setId(getId());
        courseMaterialDTO.setName(getName());
        return courseMaterialDTO;
    }
    public CourseMaterialDTO toFullContentDTO() {
        CourseMaterialDTO courseMaterialDTO = toDTO();
        courseMaterialDTO.setData(ImageUtil.decompressImage(getData()));
        return courseMaterialDTO;
    }

}
