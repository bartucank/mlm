package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.ShelfDTO;
import lombok.Data;

import java.util.List;

@Data
public class ShelfDTOListResponse {
    private List<ShelfDTO> shelfDTOList;
}
