package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.BorrowHistoryDTO;
import lombok.Data;

import java.util.List;

@Data
public class BorrowHistoryDTOListResponse {
    private List<BorrowHistoryDTO> borrowHistoryDTOList;
}
