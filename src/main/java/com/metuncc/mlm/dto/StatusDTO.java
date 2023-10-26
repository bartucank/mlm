package com.metuncc.mlm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusDTO {
    private String statusCode;
    private String msg;
}
