package com.metuncc.mlm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.DayOfWeek;

@Data
public class StatisticsDTO implements Comparable<StatisticsDTO>{
    private Long id;
    private Integer totalUserCount;
    private Integer totalBookCount;
    private Integer availableBookCount;
    private Integer unavailableBookCount;
    private BigDecimal sumOfBalance;
    private BigDecimal sumOfDebt;
    private Integer queueCount;
    private DayOfWeek day;
    private String dayDesc;
    private Integer dayInt;
    @Override
    public int compareTo(StatisticsDTO other) {
        return Long.compare(this.getId(), other.getId());
    }

}
