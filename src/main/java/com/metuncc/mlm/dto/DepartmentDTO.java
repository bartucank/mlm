package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.enums.Department;
import lombok.Data;

@Data
public class DepartmentDTO {
    private Department department;
    private String departmentString;
}
