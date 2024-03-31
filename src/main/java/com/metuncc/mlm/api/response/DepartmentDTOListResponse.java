package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.dto.DepartmentDTO;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentDTOListResponse  {
    private List<DepartmentDTO> departmentDTOList;
}
