package com.metuncc.mlm.entity;


import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.QueueStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class BookQueueHoldHistoryRecord extends MLMBaseClass {

   @ManyToOne
   private BookQueueRecord bookQueueRecord;

   private Long userId;

   private LocalDateTime endDate;
   @Override
   public String toString(){
      return "";
   }
}
