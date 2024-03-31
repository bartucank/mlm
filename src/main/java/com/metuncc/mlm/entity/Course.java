package com.metuncc.mlm.entity;


import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Course extends MLMBaseClass {

   private String name;

   @ManyToOne
   private User lecturer;

   private Boolean isPublic = false;

   @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
   private List<CourseMaterial> courseMaterialList;

}
