package com.beaconfire.history_service.service;

import com.beaconfire.history_service.dto.DataResponse;
import com.beaconfire.history_service.dto.HistoryCreateDTO;
import com.beaconfire.history_service.dto.HistoryResponseDTO;
import com.beaconfire.history_service.dto.user.UserDTO;
import com.beaconfire.history_service.entity.History;
import com.beaconfire.history_service.feign.PostFeignClient;
import com.beaconfire.history_service.feign.UserFeignClient;
import com.beaconfire.history_service.repository.HistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private ValidationService validationService;

    @Mock
    private PostFeignClient postFeignClient;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private ObjectMapper objectMapper;

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
        // Mock History Repository
        when(historyRepository.findByUserIdOrderByViewDateDesc(1))
                .thenReturn(Arrays.asList(testHistory));

        // Mock Post Response
        DataResponse postResponse = DataResponse.builder()
                .success(true)
                .data(Map.of(
                        "post", Map.of(
                                "title", "Test Post",
                                "userId", 2,
                                "accessibility", "PUBLISHED"
                        )
                ))
                .build();

        // Mock User Response
        DataResponse userResponse = DataResponse.builder()
                .success(true)
                .data(Map.of(
                        "firstName", "John",
                        "lastName", "Doe"
                ))
                .build();

        // Mock Feign Client calls
        when(postFeignClient.getPostById(testHistory.getPostId()))
                .thenReturn(postResponse);
        when(userFeignClient.getUserById(2))
                .thenReturn(userResponse);

        // Mock ObjectMapper responses
        when(objectMapper.valueToTree(postResponse.getData()))
                .thenReturn(createJsonNode(Map.of(
                        "post", Map.of(
                                "title", "Test Post",
                                "userId", 2,
                                "accessibility", "PUBLISHED"
                        )
                )));
        when(objectMapper.convertValue(any(), eq(UserDTO.class)))
                .thenReturn(UserDTO.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .build());

        List<HistoryResponseDTO> result = historyService.getUserHistory(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHistory.getHistoryId(), result.get(0).getHistoryId());
        assertEquals("Test Post", result.get(0).getPostTitle());
        assertEquals("John Doe", result.get(0).getAuthorName());
    }

    @Test
    void createHistory_NewEntry_ShouldCreateHistory() {
        when(historyRepository.findByUserIdAndPostId(1, "post123"))
                .thenReturn(Optional.empty());
        when(historyRepository.save(any(History.class)))
                .thenReturn(testHistory);
        doNothing().when(validationService)
                .validateUserAndPost(any(), any());

        // Mock Post Response for new entry
        DataResponse postResponse = DataResponse.builder()
                .success(true)
                .data(Map.of(
                        "post", Map.of(
                                "title", "Test Post",
                                "userId", 2,
                                "accessibility", "PUBLISHED"
                        )
                ))
                .build();

        // Mock User Response
        DataResponse userResponse = DataResponse.builder()
                .success(true)
                .data(Map.of(
                        "firstName", "John",
                        "lastName", "Doe"
                ))
                .build();

        // Mock Feign Client calls
        when(postFeignClient.getPostById("post123"))
                .thenReturn(postResponse);
        when(userFeignClient.getUserById(2))
                .thenReturn(userResponse);

        // Mock ObjectMapper
        when(objectMapper.valueToTree(postResponse.getData()))
                .thenReturn(createJsonNode(Map.of(
                        "post", Map.of(
                                "title", "Test Post",
                                "userId", 2,
                                "accessibility", "PUBLISHED"
                        )
                )));
        when(objectMapper.convertValue(any(), eq(UserDTO.class)))
                .thenReturn(UserDTO.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .build());

        HistoryResponseDTO result = historyService.createHistory(testCreateDTO);

        assertNotNull(result);
        assertEquals(testHistory.getHistoryId(), result.getHistoryId());
        assertEquals("Test Post", result.getPostTitle());
        assertEquals("John Doe", result.getAuthorName());
        verify(historyRepository).save(any(History.class));
    }

    private JsonNode createJsonNode(Map<String, Object> data) {
        return new ObjectMapper().valueToTree(data);
    }
}