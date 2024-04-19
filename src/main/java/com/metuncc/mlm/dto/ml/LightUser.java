package com.metuncc.mlm.dto.ml;

import com.metuncc.mlm.entity.enums.Department;
import lombok.Data;

@Data
public class LightUser {
    private Long id;
    private String name;
    private Department department;
    private String departmentStr;
}
