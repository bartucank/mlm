package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.CourseStudentDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class CourseStudent extends MLMBaseClass {

    private String studentNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public CourseStudentDTO toDTO() {
        CourseStudentDTO courseStudentDTO = new CourseStudentDTO();
        courseStudentDTO.setId(getId());
        courseStudentDTO.setStudentNumber(getStudentNumber());
        courseStudentDTO.setStudentId(getStudent().getId());
        courseStudentDTO.setStudentName(getStudent().getFullName());
        return courseStudentDTO;
    }
}
