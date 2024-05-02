package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.CopyCard;
import com.metuncc.mlm.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {


    @Query("SELECT c FROM Course c WHERE c.isPublic=true or c.id in (SELECT cs.course.id FROM CourseStudent cs WHERE cs.student.id=:id)")
    List<Course> getAllPublicCoursesAndRegisteredCourses(@Param("id") Long id);

    @Query("SELECT c FROM Course c WHERE c.lecturer.id=:id")
    List<Course> getCoursesByLecturerId(@Param("id") Long id);

}
