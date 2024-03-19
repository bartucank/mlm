package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.RoomSlotWithResDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomSlotWithResDTOListResponse {
    private List<RoomSlotWithResDTO> roomSlotWithResDTOList;
}
