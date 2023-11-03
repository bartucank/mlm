package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf,Long> {


}
