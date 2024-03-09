package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Email;
import com.metuncc.mlm.entity.enums.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email,Long>{

    @Query("select e from Email e where e.emailStatus=:emailStatus")
    List<Email> findAllEmailsByStatus(@Param("emailStatus")EmailStatus emailStatus);

    @Modifying
    @Query("update Email e set e.emailStatus='PLANNED' where e in :emails")
    void updateAllStatus(@Param("emails") List<Email> emails);
}
