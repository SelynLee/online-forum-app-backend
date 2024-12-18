package com.beaconfire.messages_service;

import com.beaconfire.messages_service.dto.MessageRequestDTO;
import com.beaconfire.messages_service.dto.MessageResponseDTO;
import com.beaconfire.messages_service.entity.Message;
import com.beaconfire.messages_service.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MessageResponseDTO createMessage(MessageRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (requestDTO.getUserId() == null ||
                requestDTO.getSubject() == null || requestDTO.getSubject().trim().isEmpty() ||
                requestDTO.getMessage() == null || requestDTO.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: userId, subject, and message are required and cannot be empty");
        }

        Message message = new Message();
        message.setUserId(requestDTO.getUserId());
        message.setEmail(requestDTO.getEmail());
        message.setSubject(requestDTO.getSubject());
        message.setMessage(requestDTO.getMessage());
        message.setStatus("open");
        message.setDateCreated(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        return mapToResponseDTO(savedMessage);
    }

    public List<MessageResponseDTO> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messages.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public MessageResponseDTO updateMessageStatus(Long id, String status) {
        if (id == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        if (!status.equalsIgnoreCase("open") && !status.equalsIgnoreCase("closed")) {
            throw new IllegalArgumentException("Invalid status value: must be 'open' or 'closed'");
        }

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        message.setStatus(status);

        Message updatedMessage = messageRepository.save(message);

        return mapToResponseDTO(updatedMessage);
    }

    public MessageResponseDTO mapToResponseDTO(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setMessageId(message.getMessageId());
        dto.setUserId(message.getUserId());
        dto.setEmail(message.getEmail());
        dto.setSubject(message.getSubject());
        dto.setMessage(message.getMessage());
        dto.setStatus(message.getStatus());
        dto.setDateCreated(message.getDateCreated().toString());
        return dto;
    }
}
