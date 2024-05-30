package org.javaacademy.one_armed_bandit.service;

import lombok.AllArgsConstructor;
import org.javaacademy.one_armed_bandit.dto.GameHistoryDTO;
import org.javaacademy.one_armed_bandit.repository.GameMachineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class GameMachineService {
    private TransactionTemplate transactionTemplate;
    private GameMachineRepository gameMachineRepository;
    private static final char[] SYMBOLS = {'A', 'F', 'D'};
    private static final BigDecimal COST_ONE_ROUND = BigDecimal.valueOf(15);
    private static final int MAX_SYMBOL = 3;
    private static final Map<String, BigDecimal> WIN_COMBINATION = Map.of(
            "AAA", BigDecimal.valueOf(10),
            "FFF", BigDecimal.valueOf(20),
            "DDD", BigDecimal.valueOf(50));
    private static final String FREE_COMBINATION = "AFD";
    private static final String FREE_COMBINATION_MESSAGE = "Бесплатный ход!";
    private static final String WIN_MESSAGE = "Вы выиграли %s";
    private static final String LOSE_MESSAGE = "Вы ничего не выиграли";
    private static final Random random = new Random();

    public String play() {
        AtomicReference<String> response = new AtomicReference<>();
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            String combination = getCombination();
            gameMachineRepository.addCombination(combination);
            Object savepoint = transactionStatus.createSavepoint();
            gameMachineRepository.addOutcome(COST_ONE_ROUND);
            if (Objects.equals(FREE_COMBINATION, combination)) {
                response.set(FREE_COMBINATION_MESSAGE);
                transactionStatus.rollbackToSavepoint(savepoint);
                return;
            }
            BigDecimal sum = checkCombination(combination);
            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                    response.set(LOSE_MESSAGE);
            } else {
                gameMachineRepository.addIncome(sum);
                response.set(WIN_MESSAGE.formatted(sum));
            }
        });
        return response.toString();
    }

    public GameHistoryDTO getHistory() {
        GameHistoryDTO gameHistoryDTO = gameMachineRepository.takeFinanceHistory().stream().findFirst().orElseThrow();
        gameHistoryDTO.setHistory(gameMachineRepository.takeLastFiveGames());
        return gameHistoryDTO;
    }

    private BigDecimal checkCombination(String combination) {
        return WIN_COMBINATION.entrySet().stream()
                .filter(winCombination -> Objects.equals(combination, winCombination.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private char getSymbol() {
        return SYMBOLS[random.nextInt(SYMBOLS.length)];
    }

    private String getCombination() {
        return Stream.generate(this::getSymbol)
                .limit(MAX_SYMBOL)
                .map(character -> Character.toString(character))
                .collect(Collectors.joining());
    }
}
