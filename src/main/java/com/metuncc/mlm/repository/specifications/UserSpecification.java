package com.metuncc.mlm.repository.specifications;

import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.Role;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {

    private Role role;
    private String fullName;
    private String username;
    private Boolean verified;
    private String email;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(role)){
            predicates.add(criteriaBuilder.equal(root.get("role"),role));
        }
        if(Objects.nonNull(fullName)){
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("fullName")),"%"+fullName.toLowerCase()+"%"
                    )
            );
        }
        if(Objects.nonNull(username)){
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("username")),"%"+username.toLowerCase()+"%"
                    )
            );
        }
        if(Objects.nonNull(verified)){
            predicates.add(criteriaBuilder.equal(root.get("verified"),verified));
        }
        if(Objects.nonNull(email)){
            predicates.add(
                    criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),"%"+email.toLowerCase()+"%"
                    )
            );
        }
        Predicate finalPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        return finalPredicate;
    }
}
