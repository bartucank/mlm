package com.metuncc.mlm.api.response;


import com.metuncc.mlm.dto.ReceiptHistoryDTO;
import lombok.Data;

import java.util.List;

@Data
public class ReceiptHistoryDTOListResponse  {
    private List<ReceiptHistoryDTO> receiptHistoryDTOList;
}
