package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Room;
import com.metuncc.mlm.entity.RoomSlot;
import com.metuncc.mlm.entity.enums.RoomSlotDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface RoomSlotRepository extends JpaRepository<RoomSlot,Long> {


    @Query("select r from RoomSlot r where r.day=:roomSlotDay and r.startHour=:localTime")
    List<RoomSlot> getRoomSlotsByTimeAndDay(@Param("localTime") LocalTime localTime,
                                            @Param("roomSlotDay") RoomSlotDays roomSlotDay);
    @Query("select r from RoomSlot r where r.day=:roomSlotDay")
    List<RoomSlot> getRoomSlotsByDay(@Param("roomSlotDay")RoomSlotDays previousDay);
}
