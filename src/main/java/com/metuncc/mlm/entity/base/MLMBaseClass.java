package com.metuncc.mlm.entity.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(EntityListener.class)
public abstract class MLMBaseClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    private Long id;

    private LocalDateTime deletedDate;
    private Boolean deleted;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;


    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Override
    public String toString(){
        return null;
    }

    @Override
    public boolean equals(Object obj){
        return false;
    }

    @Override
    public int hashCode(){
        return 0;
    }
}
