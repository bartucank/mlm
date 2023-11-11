package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Room;
import com.metuncc.mlm.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {


}
