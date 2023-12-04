package com.metuncc.mlm.repository.specifications;

import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.BookHistory;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.BookStatus;
import org.springframework.data.jpa.domain.Specification;
import lombok.AllArgsConstructor;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class BookBorrowHistorySpecification implements Specification<BookHistory> {


    private LocalDateTime returnDate;
    private BookStatus status;

    @Override
    public Predicate toPredicate(Root<BookHistory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        List<Predicate> predicateList = new ArrayList<>();
        if(Objects.nonNull(returnDate)){
            predicateList.add(
                    criteriaBuilder.equal(
                            criteriaBuilder.lower(root.get("returnDate")), returnDate));
        }
        if(Objects.nonNull(status)){
            predicateList.add(
                    criteriaBuilder.equal(root.get("status"),status)
            );
        }
        Predicate finalPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        return finalPredicate;
    }
}
