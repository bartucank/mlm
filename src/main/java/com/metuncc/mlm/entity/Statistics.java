package com.metuncc.mlm.entity;

import com.metuncc.mlm.dto.CopyCardDTO;
import com.metuncc.mlm.dto.StatisticsDTO;
import com.metuncc.mlm.entity.base.MLMBaseClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.DayOfWeek;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Statistics extends MLMBaseClass {

    private Integer totalUserCount;
    private Integer totalBookCount;
    private Integer availableBookCount;
    private Integer unavailableBookCount;
    private BigDecimal sumOfBalance;
    private BigDecimal sumOfDebt;
    private Integer queueCount;

    @Enumerated(value = EnumType.STRING)
    private DayOfWeek day;

    public StatisticsDTO toDTO(){
        StatisticsDTO dto = new StatisticsDTO();
        dto.setId(getId());
        dto.setTotalUserCount(getTotalUserCount());
        dto.setTotalBookCount(getTotalBookCount());
        dto.setAvailableBookCount(getAvailableBookCount());
        dto.setUnavailableBookCount(getUnavailableBookCount());
        dto.setSumOfBalance(getSumOfBalance());
        dto.setSumOfDebt(getSumOfDebt());
        dto.setQueueCount(getQueueCount());
        dto.setDay(getDay());
        dto.setDayDesc(getDay().name());
        dto.setDayInt(getDay().getValue());
        return dto;
    }
    @Override
    public String toString(){
        return "";
    }
}
