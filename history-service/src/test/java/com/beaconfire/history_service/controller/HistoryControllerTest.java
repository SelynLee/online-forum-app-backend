package com.beaconfire.history_service.controller;

import com.beaconfire.history_service.dto.HistoryCreateDTO;
import com.beaconfire.history_service.dto.HistoryResponseDTO;
import com.beaconfire.history_service.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private HistoryController historyController;

    private HistoryResponseDTO testResponseDTO;
    private HistoryCreateDTO testCreateDTO;

    @BeforeEach
    void setUp() {
        testResponseDTO = HistoryResponseDTO.builder()
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
        HistoryResponseDTO testResponseDTO = HistoryResponseDTO.builder()
                .historyId(1)
                .userId(1)
                .postId("post123")
                .viewDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .postTitle("Test Post")
                .authorId(2)
                .authorName("John Doe")
                .build();

        when(historyService.getUserHistory(1))
                .thenReturn(Arrays.asList(testResponseDTO));

        ResponseEntity<List<HistoryResponseDTO>> response =
                historyController.getUserHistory(1);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(historyService).getUserHistory(1);
    }

    @Test
    void createHistory_ShouldReturnCreatedHistory() {
        when(historyService.createHistory(any(HistoryCreateDTO.class)))
                .thenReturn(testResponseDTO);

        ResponseEntity<HistoryResponseDTO> response =
                historyController.createHistory(testCreateDTO);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(testResponseDTO.getHistoryId(),
                response.getBody().getHistoryId());
        verify(historyService).createHistory(testCreateDTO);
    }
}
