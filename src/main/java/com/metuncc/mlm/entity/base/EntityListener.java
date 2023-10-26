package com.metuncc.mlm.entity.base;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
// extends AuditingEntityListener ?
public class EntityListener {
    @PrePersist
    public void prePersist(Object target) {
        if (target instanceof MLMBaseClass) {
            MLMBaseClass entity = (MLMBaseClass) target;
            entity.setCreatedDate(LocalDateTime.now());
        }
    }

    @PreUpdate
    public void preUpdate(Object target) {
        if (target instanceof MLMBaseClass) {
            MLMBaseClass entity = (MLMBaseClass) target;
            entity.setLastModifiedDate(LocalDateTime.now());
        }
    }
}
