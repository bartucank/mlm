package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.CopyCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CopyCardRepository extends JpaRepository<CopyCard,Long>, JpaSpecificationExecutor<CopyCard> {


    @Query("select c from CopyCard c where c.owner.id=:id")
    CopyCard getByUser(@Param("id")Long id);
}
