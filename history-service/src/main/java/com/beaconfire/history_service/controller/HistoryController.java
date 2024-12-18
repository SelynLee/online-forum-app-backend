package com.beaconfire.history_service.controller;

import com.beaconfire.history_service.dto.HistoryCreateDTO;
import com.beaconfire.history_service.dto.HistoryResponseDTO;
import com.beaconfire.history_service.service.HistoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/api/history")
@Slf4j
public class HistoryController {
    private final HistoryService historyService;

    @Value("${user.role}")
    private String userRole;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HistoryResponseDTO>> getUserHistory(
            @PathVariable Integer userId) {
        log.info("Received request to get history for user {}", userId);
        return ResponseEntity.ok(historyService.getUserHistory(userId));
    }

    @GetMapping("/config")
    public ResponseEntity<String> getConfig() {
        log.info("Received request to get configuration");
        return ResponseEntity.ok(userRole);
    }

    @PostMapping
    public ResponseEntity<HistoryResponseDTO> createHistory(
            @Valid @RequestBody HistoryCreateDTO dto) {
        log.info("Received request to create history for user {} and post {}",
                dto.getUserId(), dto.getPostId());
        return ResponseEntity.ok(historyService.createHistory(dto));
    }
}