package com.beaconfire.history_service.service;

import com.beaconfire.history_service.dto.HistoryCreateDTO;
import com.beaconfire.history_service.dto.HistoryResponseDTO;
import com.beaconfire.history_service.entity.History;
import com.beaconfire.history_service.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private HistoryService historyService;

    private History testHistory;
    private HistoryCreateDTO testCreateDTO;

    @BeforeEach
    void setUp() {
        testHistory = History.builder()
                .historyId(1)
                .userId(1)
                .postId("post123")
                .viewDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testCreateDTO = HistoryCreateDTO.builder()
                .userId(1)
                .postId("post123")
                .build();
    }

    @Test
    void getUserHistory_ShouldReturnHistoryList() {
        when(historyRepository.findByUserIdOrderByViewDateDesc(1))
                .thenReturn(Arrays.asList(testHistory));

        List<HistoryResponseDTO> result = historyService.getUserHistory(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHistory.getHistoryId(), result.get(0).getHistoryId());
        verify(historyRepository).findByUserIdOrderByViewDateDesc(1);
    }

    @Test
    void createHistory_NewEntry_ShouldCreateHistory() {
        when(historyRepository.findByUserIdAndPostId(1, "post123"))
                .thenReturn(Optional.empty());
        when(historyRepository.save(any(History.class)))
                .thenReturn(testHistory);
        doNothing().when(validationService)
                .validateUserAndPost(any(), any());

        HistoryResponseDTO result = historyService.createHistory(testCreateDTO);

        assertNotNull(result);
        assertEquals(testHistory.getHistoryId(), result.getHistoryId());
        verify(historyRepository).save(any(History.class));
    }
}