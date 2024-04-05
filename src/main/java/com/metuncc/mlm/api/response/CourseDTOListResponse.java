package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.CourseDTO;
import lombok.Data;

import java.util.List;

@Data
public class CourseDTOListResponse {
    private List<CourseDTO> courseDTOList;
}
