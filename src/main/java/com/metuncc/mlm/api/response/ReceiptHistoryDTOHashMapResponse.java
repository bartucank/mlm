package com.metuncc.mlm.api.response;


import com.metuncc.mlm.dto.ReceiptHistoryDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ReceiptHistoryDTOHashMapResponse extends  PageableResponse {
    private HashMap<Long, List<ReceiptHistoryDTO>> receiptHistoryHashMap;
}
