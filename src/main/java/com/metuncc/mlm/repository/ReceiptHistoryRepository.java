package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.ReceiptHistory;
import com.metuncc.mlm.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptHistoryRepository extends JpaRepository<ReceiptHistory,Long> {


    @Query("select r from ReceiptHistory  r where r.user.id=:id")
    List<ReceiptHistory> getByUserId(@Param("id") Long id);
}
