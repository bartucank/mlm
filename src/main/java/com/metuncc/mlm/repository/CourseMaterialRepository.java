package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.CopyCard;
import com.metuncc.mlm.entity.Course;
import com.metuncc.mlm.entity.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial,Long> {


}
