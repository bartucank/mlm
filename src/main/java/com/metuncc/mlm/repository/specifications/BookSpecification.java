package com.metuncc.mlm.repository.specifications;

import antlr.collections.impl.BitSet;
import com.metuncc.mlm.entity.Book;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class BookSpecification implements Specification<Book> {

    private String name;
    private String author;
    private String publisher;
    private String description;
    private String isbn;
    private LocalDate publicationDate;
    private String barcode;

    private BookCategory category;
    private BookStatus status;
    private Boolean ebookAvailable;

    @Override
    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        if(Objects.nonNull(ebookAvailable) && ebookAvailable){
            predicateList.add(
                    criteriaBuilder.isNotNull(root.get("ebook"))
            );
        }
        if(Objects.nonNull(name)){
            predicateList.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")), "%"+name.toLowerCase()+"%"
                    )
            );
        }
        if(Objects.nonNull(author)){
            predicateList.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("author")), "%"+author.toLowerCase()+"%"
                    )
            );
        }
        if(Objects.nonNull(publisher)){
            predicateList.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("publisher")),"%"+publisher.toLowerCase()+"%"
                    )
            );
        }
        if(Objects.nonNull(description)){
            predicateList.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),"%"+description.toLowerCase()+"%"
                    )
            );
        }
        if(Objects.nonNull(isbn)){
            predicateList.add(
                    criteriaBuilder.like(root.get("isbn"),"%"+isbn+"%")
            );
        }
        if(Objects.nonNull(publicationDate)){
            predicateList.add(
                    criteriaBuilder.equal(root.get("publicationDate"),publicationDate));
        }
        if(Objects.nonNull(barcode)){
            predicateList.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get(barcode)),"%"+barcode.toLowerCase()+"%"
                    )
            );
        }
        if(Objects.nonNull(category)){
            predicateList.add(
                    criteriaBuilder.equal(root.get("category"),category)
            );
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
