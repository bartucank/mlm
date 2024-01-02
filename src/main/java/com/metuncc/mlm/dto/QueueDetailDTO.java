package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.BookQueueHoldHistoryRecord;
import com.metuncc.mlm.entity.enums.QueueStatus;
import lombok.Data;

import java.util.List;

@Data
public class QueueDetailDTO {
    private BookDTO bookDTO;

    private List<QueueMembersDTO> members;
    private QueueStatus status;
    private String statusDesc;

    private Boolean holdFlag;
    private BookQueueHoldHistoryRecordDTO holdDTO;

    private String startDate;


}
