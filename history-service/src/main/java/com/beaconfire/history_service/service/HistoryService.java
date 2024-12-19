package com.beaconfire.history_service.service;

import com.beaconfire.history_service.dto.DataResponse;
import com.beaconfire.history_service.dto.HistoryCreateDTO;
import com.beaconfire.history_service.dto.HistoryResponseDTO;
import com.beaconfire.history_service.dto.user.UserDTO;
import com.beaconfire.history_service.entity.History;
import com.beaconfire.history_service.feign.PostFeignClient;
import com.beaconfire.history_service.feign.UserFeignClient;
import com.beaconfire.history_service.repository.HistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final PostFeignClient postFeignClient;
    private final UserFeignClient userFeignClient;
    private final ObjectMapper objectMapper;

    public List<HistoryResponseDTO> getUserHistory(Integer userId) {
        log.info("Fetching history for user {}", userId);
        return historyRepository.findByUserIdOrderByViewDateDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .filter(dto -> dto != null)
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
        HistoryResponseDTO dto = new HistoryResponseDTO();
        BeanUtils.copyProperties(history, dto);

        try {
            log.debug("Fetching post details for postId: {}", history.getPostId());
            DataResponse postResponse = postFeignClient.getPostById(history.getPostId());

            if (postResponse.isSuccess() && postResponse.getData() != null) {
                JsonNode postData = objectMapper.valueToTree(postResponse.getData());
                log.debug("Post response data: {}", postData);

                JsonNode postNode = postData.get("post");
                if (postNode != null) {
                    String accessibility = postNode.get("accessibility").asText();
                    log.debug("Post accessibility: {}", accessibility);

                    if ("PUBLISHED".equals(accessibility)) {
                        dto.setPostTitle(postNode.get("title").asText());
                        Integer authorId = postNode.get("userId").asInt();
                        dto.setAuthorId(authorId);

                        try {
                            DataResponse authorResponse = userFeignClient.getUserById(authorId);
                            if (authorResponse.isSuccess() && authorResponse.getData() != null) {
                                UserDTO author = objectMapper.convertValue(authorResponse.getData(), UserDTO.class);
                                dto.setAuthorName(author.getFirstName() + " " + author.getLastName());
                                return dto;
                            }
                        } catch (Exception e) {
                            log.error("Error fetching author details: {}", e.getMessage());
                            dto.setAuthorName("Unknown Author");
                            return dto;
                        }
                    } else {
                        log.debug("Post is not published: {}", accessibility);
                        return null;
                    }
                } else {
                    log.error("Could not find post data in response");
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("Error processing post {}: {}", history.getPostId(), e.getMessage(), e);
            return null;
        }

        return null;
    }
}