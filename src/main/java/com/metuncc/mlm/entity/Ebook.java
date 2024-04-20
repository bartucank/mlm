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
public class Ebook extends MLMBaseClass {

    private String name;

    @Lob
    private byte[] data;

    private String fileName;
    private String extension;


    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;


}
