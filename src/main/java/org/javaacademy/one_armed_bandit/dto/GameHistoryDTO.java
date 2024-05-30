package org.javaacademy.one_armed_bandit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GameHistoryDTO {
    private BigDecimal income;
    private BigDecimal outcome;
    private List<String> history;
}
