package com.metuncc.mlm.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsDTO {
    private Integer totalUserCount;
    private Integer totalBookCount;
    private Integer availableBookCount;
    private Integer unavailableBookCount;
    private BigDecimal sumOfBalance;
    private BigDecimal sumOfDebt;
    private Integer queueCount;


}
