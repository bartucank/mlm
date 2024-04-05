package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.CopyCard;
import com.metuncc.mlm.entity.CourseMaterial;
import com.metuncc.mlm.entity.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseStudentRepository extends JpaRepository<CourseStudent,Long> {


    @Query("SELECT cs FROM CourseStudent cs WHERE cs.studentNumber=:studentNumber")
    List<CourseStudent> getByStudentId(@Param("studentNumber") String studentNumber);

}
