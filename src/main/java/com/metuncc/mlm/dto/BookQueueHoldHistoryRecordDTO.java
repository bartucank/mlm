package com.metuncc.mlm.dto;


import com.metuncc.mlm.entity.BookQueueRecord;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
public class BookQueueHoldHistoryRecordDTO extends MLMBaseClass {

   private Long userId;

   private String endDate;

}
