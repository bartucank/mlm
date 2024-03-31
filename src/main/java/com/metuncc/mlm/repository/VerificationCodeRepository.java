package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.VerificationCode;
import com.metuncc.mlm.entity.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode,Long> {


    @Query("select v from VerificationCode v where v.user=:user and v.verificationType=:verificationType ")
    VerificationCode getByUserAndType(@Param("user") User user,
                                      @Param("verificationType")VerificationType verificationType);

    @Query("select v from VerificationCode v where v.code=:code ")
    VerificationCode getByCode(@Param("code") String code);
}
