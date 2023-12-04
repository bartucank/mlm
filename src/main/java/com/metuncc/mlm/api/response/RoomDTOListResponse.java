package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.RoomDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomDTOListResponse {
    private List<RoomDTO> roomDTOList;
}
