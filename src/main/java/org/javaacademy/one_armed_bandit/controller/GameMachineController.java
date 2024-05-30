package org.javaacademy.one_armed_bandit.controller;

import lombok.RequiredArgsConstructor;
import org.javaacademy.one_armed_bandit.dto.GameHistoryDTO;
import org.javaacademy.one_armed_bandit.service.GameMachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameMachineController {
    private final GameMachineService gameMachineService;

    @PostMapping("/play")
    public String play() {
        return gameMachineService.play();
    }

    @GetMapping("/history")
    public ResponseEntity<GameHistoryDTO> history() {
        return ResponseEntity.ok(gameMachineService.getHistory());
    }
}
