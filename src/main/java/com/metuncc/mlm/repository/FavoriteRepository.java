package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite,Long>, JpaSpecificationExecutor<Favorite> {
    @Query("select f from Favorite f where f.userId.id = :userId")
    List<Favorite> findByUserId(@Param("userId") Long userId);

}
