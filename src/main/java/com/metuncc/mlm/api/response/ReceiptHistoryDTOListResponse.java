package com.metuncc.mlm.api.response;


import com.metuncc.mlm.dto.ReceiptHistoryDTO;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ReceiptHistoryDTOListResponse extends PageableResponse {
    private List<ReceiptHistoryDTO> receiptHistoryDTOList;
}
