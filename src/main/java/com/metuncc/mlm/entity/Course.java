package com.metuncc.mlm.entity;


import com.metuncc.mlm.dto.CourseDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Course extends MLMBaseClass {

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User lecturer;

    private Boolean isPublic = false;

    @OneToOne
    private Image imageId;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseMaterial> courseMaterialList;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseStudent> courseStudentList;


    public CourseDTO toDTOForUsers() {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(getId());
        courseDTO.setName(getName());
        courseDTO.setLecturerId(getLecturer().getId());
        courseDTO.setLecturerName(getLecturer().getFullName());
        courseDTO.setIsPublic(getIsPublic());
        courseDTO.setImageId(getImageId().getId());
        courseDTO.setCourseMaterialDTOList(getCourseMaterialList().stream().map(CourseMaterial::toDTO).collect(Collectors.toList()));
        return courseDTO;
    }
    public CourseDTO toDTOForLecturer() {
        CourseDTO courseDTO =toDTOForUsers();
        courseDTO.setCourseStudentDTOList(getCourseStudentList().stream().map(CourseStudent::toDTO).collect(Collectors.toList()));
        return courseDTO;
    }
}
