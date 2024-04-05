package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.CourseStudent;
import lombok.Data;

import java.util.List;

@Data
public class CourseDTO {
    private Long id;
    private String name;
    private Long lecturerId;
    private String lecturerName;
    private Boolean isPublic;
    private Long imageId;
    private List<CourseMaterialDTO> courseMaterialDTOList;
    private List<CourseStudentDTO> courseStudentDTOList;
}
