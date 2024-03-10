package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.ReceiptHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptHistoryRepository extends JpaRepository<ReceiptHistory,Long> {


    @Query("select r from ReceiptHistory  r where r.user.id=:id")
    List<ReceiptHistory> getByUserId(@Param("id") Long id);

    @Query("select r from ReceiptHistory r where r.approved = :approved")
    Page<ReceiptHistory> getByStatus(@Param("approved") Boolean approved, Pageable pageable);
}
