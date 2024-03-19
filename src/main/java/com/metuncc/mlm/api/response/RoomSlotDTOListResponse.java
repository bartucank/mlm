package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.RoomSlotDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomSlotDTOListResponse {
    private List<RoomSlotDTO> roomSlotDTOList;
}
