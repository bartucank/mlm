package com.metuncc.mlm.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
public class MLMBaseClassDTO {
    private Long id;
    private LocalDateTime deletedDate;
    private Boolean deleted;
    private Long creator;
    private Long lastModifierUser;
}
