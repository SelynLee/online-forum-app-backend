package com.beaconfire.messages_service;

import com.beaconfire.messages_service.dto.MessageRequestDTO;
import com.beaconfire.messages_service.dto.MessageResponseDTO;
import com.beaconfire.messages_service.entity.Message;
import com.beaconfire.messages_service.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testCreateMessage() {
        // Arrange
        MessageRequestDTO requestDTO = new MessageRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setEmail("test@example.com");
        requestDTO.setSubject("Test Subject");
        requestDTO.setMessage("Test Message");

        Message savedMessage = new Message();
        savedMessage.setMessageId(1L);
        savedMessage.setUserId(1L);
        savedMessage.setEmail("test@example.com");
        savedMessage.setSubject("Test Subject");
        savedMessage.setMessage("Test Message");
        savedMessage.setStatus("open");
        savedMessage.setDateCreated(LocalDateTime.now());

        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // Act
        MessageResponseDTO responseDTO = messageService.createMessage(requestDTO);

        // Assert
        assertEquals("Test Subject", responseDTO.getSubject());
        assertEquals("open", responseDTO.getStatus());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void testGetAllMessages() {
        // Arrange
        Message message1 = new Message();
        message1.setMessageId(1L);
        message1.setUserId(1L);
        message1.setEmail("test1@example.com");
        message1.setSubject("Subject 1");
        message1.setMessage("Message 1");
        message1.setStatus("open");
        message1.setDateCreated(LocalDateTime.now());

        Message message2 = new Message();
        message2.setMessageId(2L);
        message2.setUserId(2L);
        message2.setEmail("test2@example.com");
        message2.setSubject("Subject 2");
        message2.setMessage("Message 2");
        message2.setStatus("closed");
        message2.setDateCreated(LocalDateTime.now());

        when(messageRepository.findAll()).thenReturn(Arrays.asList(message1, message2));

        // Act
        List<MessageResponseDTO> responseList = messageService.getAllMessages();

        // Assert
        assertEquals(2, responseList.size());
        assertEquals("Subject 1", responseList.get(0).getSubject());
        assertEquals("closed", responseList.get(1).getStatus());
        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void testUpdateMessageStatus() {
        // Arrange
        Long messageId = 1L;
        String newStatus = "closed";

        Message existingMessage = new Message();
        existingMessage.setMessageId(messageId);
        existingMessage.setStatus("open");
        existingMessage.setDateCreated(LocalDateTime.now());

        Message updatedMessage = new Message();
        updatedMessage.setMessageId(messageId);
        updatedMessage.setStatus(newStatus);
        updatedMessage.setDateCreated(LocalDateTime.now());

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(updatedMessage);

        // Act
        MessageResponseDTO responseDTO = messageService.updateMessageStatus(messageId, newStatus);

        // Assert
        assertEquals(newStatus, responseDTO.getStatus());
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, times(1)).save(any(Message.class));
    }
}

