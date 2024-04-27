package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Room;
import com.metuncc.mlm.entity.RoomReservation;
import com.metuncc.mlm.entity.RoomSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RoomReservationRepository extends JpaRepository<RoomReservation,Long> {

    @Query("select r from RoomReservation  r where r.userId=:id and r.date=:date")
    List<RoomReservation> getRoomReservationByUserId(@Param("id") Long id,
                                                     @Param("date")LocalDate date);


    @Query("select r from RoomReservation  r where r.userId=:id and r.roomSlot.startHour=:date")
    List<RoomReservation> getRoomReservationByUserIdAndDate(@Param("id") Long id,@Param("date") LocalTime date);


    @Query("select r from RoomReservation r where r.roomSlot in :roomSlotList and r.approved=false")
    List<RoomReservation> getUnapprovedRoomReservationByRoomSlotList(@Param("roomSlotList") List<RoomSlot> roomSlotList);


    @Query("select r from RoomReservation r where r.roomSlot in :roomSlotList ")
    List<RoomReservation> findAllByRoomSlotList(@Param("roomSlotList") List<RoomSlot> roomSlotList);

    @Query("select r from RoomReservation r where r.roomSlot.id =:roomSlot")
    RoomReservation findByRoomSlot(@Param("roomSlot") Long roomSlot);
}
