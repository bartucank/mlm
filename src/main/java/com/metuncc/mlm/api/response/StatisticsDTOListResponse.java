package com.metuncc.mlm.api.response;

import com.metuncc.mlm.dto.BookDTO;
import com.metuncc.mlm.dto.StatisticsDTO;
import lombok.Data;

import java.util.List;

@Data
public class StatisticsDTOListResponse {
    private List<StatisticsDTO> statisticsDTOList;
}
