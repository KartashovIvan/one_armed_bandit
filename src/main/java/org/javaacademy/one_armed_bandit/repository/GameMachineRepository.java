package org.javaacademy.one_armed_bandit.repository;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.javaacademy.one_armed_bandit.dto.GameHistoryDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
public class GameMachineRepository {
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        String sqlClearGameHistory = "truncate game RESTART identity";
        String sqlDropValueFromFinanceResult = "truncate finance_result;";
        String sqlClearFinanceResult = "insert into finance_result (income,outcome) values (0,0)";
        jdbcTemplate.update(sqlClearGameHistory);
        jdbcTemplate.update(sqlDropValueFromFinanceResult);
        jdbcTemplate.update(sqlClearFinanceResult);
    }

    public void addOutcome(BigDecimal outcome) {
        String sqlAddOutcome = """
                update finance_result set outcome =
                (select outcome from finance_result) + ?
                """;
        jdbcTemplate.update(sqlAddOutcome,
                preparedStatement -> preparedStatement.setBigDecimal(1, outcome));
    }

    public void addCombination(String combination) {
        String[] arrayCombination = combination.split("");
        String sqlAddCombination = "insert into public.game (sym_first,sym_second,sym_third) values (?,?,?)";
        jdbcTemplate.update(sqlAddCombination,
                preparedStatement -> {
                    preparedStatement.setString(1, arrayCombination[0]);
                    preparedStatement.setString(2, arrayCombination[1]);
                    preparedStatement.setString(3, arrayCombination[2]);
                });
    }

    public void addIncome(BigDecimal sum) {
        String sqlAddIncome = "update finance_result set income  = (select income from finance_result) + ?";
        jdbcTemplate.update(sqlAddIncome,
                preparedStatement -> preparedStatement.setBigDecimal(1, sum));
    }

    public List<GameHistoryDTO> takeFinanceHistory() {
        String sqlFinanceHistory = "select * from finance_result";
        return jdbcTemplate.query(sqlFinanceHistory, (resultSet, rowNumber) ->{
            GameHistoryDTO gameHistoryDTO = new GameHistoryDTO();
            gameHistoryDTO.setIncome(resultSet.getBigDecimal("income"));
            gameHistoryDTO.setOutcome(resultSet.getBigDecimal("outcome"));
            return gameHistoryDTO;
        });
    }

    public List<String> takeLastFiveGames() {
        String sqlLastFiveGame = "select sym_first,sym_second, sym_third from game ORDER BY id desc limit 5";
        return jdbcTemplate.query(sqlLastFiveGame, (resultSet, rowNumber) ->
                    resultSet.getString("sym_first") +
                    resultSet.getString("sym_second") +
                    resultSet.getString("sym_third"));
    }
}
