package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query ("select sum(u.debt) from User u")
    BigDecimal totalDebt();

    @Query("select c from User c where c.role=:role")
    List<User> findAllByRoles(@Param("role") Role role);

    @Query("select c from User c where (:email is not null and c.email=:email) and (:username is not null and c.username=:username) ")
    User getByEmailOrUsername(@Param("email") String email, @Param("username")String username);

    @Query("select c from User c where c.studentNumber=:studentNumber")
    User findByStudentNumber(@Param("studentNumber") String studentNumber);
}
