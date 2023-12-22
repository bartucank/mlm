package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> , JpaSpecificationExecutor<User> {
    @Query("select u from User u where u.username=:username")
    User findByUsername(@Param("username") String username);

    @Query("select u from User u where u.id=:id")
    User getById(@Param("id") Long id);


    @Query("select u from User u where u.email=:email")
    User findByEmail(@Param("email") String email);

    @Query("select count(u) from User u")
    Integer totalUserCount();
}
