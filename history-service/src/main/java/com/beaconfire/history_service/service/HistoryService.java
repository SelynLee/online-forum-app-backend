package com.beaconfire.history_service.service;

import com.beaconfire.history_service.dto.HistoryCreateDTO;
import com.beaconfire.history_service.dto.HistoryResponseDTO;
import com.beaconfire.history_service.entity.History;
import com.beaconfire.history_service.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final ValidationService validationService;

    public List<HistoryResponseDTO> getUserHistory(Integer userId) {
        log.info("Fetching history for user {}", userId);
        return historyRepository.findByUserIdOrderByViewDateDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HistoryResponseDTO createHistory(HistoryCreateDTO dto) {
        log.info("Creating/Updating history for user {} and post {}",
                dto.getUserId(), dto.getPostId());

        validationService.validateUserAndPost(dto.getUserId(), dto.getPostId());

        var existingHistory = historyRepository.findByUserIdAndPostId(
                dto.getUserId(), dto.getPostId());

        History history = existingHistory.orElseGet(() -> {
            var newHistory = new History();
            newHistory.setUserId(dto.getUserId());
            newHistory.setPostId(dto.getPostId());
            return newHistory;
        });

        history.setViewDate(LocalDateTime.now());
        return convertToDTO(historyRepository.save(history));
    }

    private HistoryResponseDTO convertToDTO(History history) {
        var dto = new HistoryResponseDTO();
        BeanUtils.copyProperties(history, dto);
        return dto;
    }
}