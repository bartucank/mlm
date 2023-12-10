package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.BookQueueHoldHistoryRecord;
import com.metuncc.mlm.entity.RoomReservation;
import com.metuncc.mlm.entity.RoomSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookQueueHoldHistoryRecordRepository extends JpaRepository<BookQueueHoldHistoryRecord,Long> {

    @Query("select b from BookQueueHoldHistoryRecord b where b.endDate<:endDate")
    List<BookQueueHoldHistoryRecord> getBookQueueHoldHistoryRecordByEndDate(@Param("endDate")LocalDateTime endDate);
}
