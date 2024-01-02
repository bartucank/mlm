package com.metuncc.mlm.repository;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.BookBorrowHistory;
import com.metuncc.mlm.entity.Statistics;
import com.metuncc.mlm.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics,Long>, JpaSpecificationExecutor<Statistics> {


}
