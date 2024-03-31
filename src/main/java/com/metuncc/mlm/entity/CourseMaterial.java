package com.metuncc.mlm.entity;


import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class CourseMaterial extends MLMBaseClass {

   private String name;

   @OneToOne
   private Image document;


   @ManyToOne
   @JoinColumn(name = "course_id")
   private Course course;

}
