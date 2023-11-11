package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    @Query("select i from Image i where i.id=:id")
    Image getImageById(@Param("id")Long id);
}
