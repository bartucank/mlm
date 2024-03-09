package com.metuncc.mlm.entity;


import com.metuncc.mlm.entity.base.MLMBaseClass;
import com.metuncc.mlm.entity.enums.EmailStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "email")
public class Email extends MLMBaseClass {
    private String toEmail;

    @Column(columnDefinition = "TEXT")
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Enumerated(value = EnumType.STRING)
    private EmailStatus emailStatus;

    private LocalDateTime lastTryDate;
    private LocalDateTime completedDate;
    private Long tryCount;

    public Email set(String to,
                     String subject,
                     String content,
                     String title) {
        setTitle(title);
        setContent(content);
        setSubject(subject);
        setToEmail(to);
        setEmailStatus(EmailStatus.SCHEDULED);
        return this;
    }
}
