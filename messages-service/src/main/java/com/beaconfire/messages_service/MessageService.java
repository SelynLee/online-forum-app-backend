package com.beaconfire.messages_service;

import com.beaconfire.messages_service.dto.MessageRequestDTO;
import com.beaconfire.messages_service.dto.MessageResponseDTO;
import com.beaconfire.messages_service.dto.MessageStatusUpdateDTO;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public MessageResponseDTO createMessage(MessageRequestDTO requestDTO) {
        Message message = new Message();
        message.setUserId(requestDTO.getUserId());
        message.setEmail(requestDTO.getEmail());
        message.setSubject(requestDTO.getSubject());
        message.setMessage(requestDTO.getMessage());
        message.setStatus("open");

        Message savedMessage = messageRepository.save(message);

        return mapToResponseDTO(savedMessage);
    }

    public List<MessageResponseDTO> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messages.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public MessageResponseDTO updateMessageStatus(Long id, String status) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setStatus(status);
        Message updatedMessage = messageRepository.save(message);
        return mapToResponseDTO(updatedMessage);
    }

    private MessageResponseDTO mapToResponseDTO(Message message) {
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