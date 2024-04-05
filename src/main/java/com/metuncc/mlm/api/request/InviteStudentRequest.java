package com.metuncc.mlm.api.request;

import lombok.Data;

@Data
public class InviteStudentRequest {

    private Long courseId;
    private String studentNumber;

}
