package com.beaconfire.messages_service;

import com.beaconfire.messages_service.dto.MessageRequestDTO;
import com.beaconfire.messages_service.dto.MessageResponseDTO;
import com.beaconfire.messages_service.entity.Message;
import com.beaconfire.messages_service.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Clean up mocks
    }


    @Test
    void testCreateMessage() {
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

        MessageResponseDTO responseDTO = messageService.createMessage(requestDTO);

        assertEquals("Test Subject", responseDTO.getSubject());
        assertEquals("open", responseDTO.getStatus());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    public void testCreateMessage_NullRequest() {
        MessageRequestDTO requestDTO = null;

        assertThrows(IllegalArgumentException.class, () -> {
            messageService.createMessage(requestDTO);
        });
        verify(messageRepository, times(0)).save(any(Message.class));
    }

    @Test
    public void testCreateMessage_InvalidInput() {
        MessageRequestDTO requestDTO = new MessageRequestDTO(); // Missing fields

        assertThrows(IllegalArgumentException.class, () -> {
            messageService.createMessage(requestDTO);
        });
        verify(messageRepository, times(0)).save(any(Message.class));
    }

    @Test
    public void testCreateMessage_EmptyFields() {
        MessageRequestDTO requestDTO = new MessageRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setEmail("test@example.com");
        requestDTO.setSubject(""); // Empty subject
        requestDTO.setMessage(""); // Empty message

        assertThrows(IllegalArgumentException.class, () -> {
            messageService.createMessage(requestDTO);
        });
        verify(messageRepository, times(0)).save(any(Message.class));
    }


    @Test
    public void testCreateMessage_DatabaseException() {
        MessageRequestDTO requestDTO = new MessageRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setEmail("test@example.com");
        requestDTO.setSubject("Test Subject");
        requestDTO.setMessage("Test Message");

        when(messageRepository.save(any(Message.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            messageService.createMessage(requestDTO);
        });
        verify(messageRepository, times(1)).save(any(Message.class));
    }


    @Test
    public void testGetAllMessages() {
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
    public void testUpdateMessageStatus() {
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

    @Test
    public void testGetAllMessages_EmptyList() {
        // Arrange
        when(messageRepository.findAll()).thenReturn(Arrays.asList()); // Empty list

        // Act
        List<MessageResponseDTO> responseList = messageService.getAllMessages();

        // Assert
        assertEquals(0, responseList.size());
        verify(messageRepository, times(1)).findAll();
    }


    @Test
    public void testUpdateMessageStatus_MissingMessageId() {
        // Arrange
        Long invalidMessageId = 999L; // Non-existent messageId
        String newStatus = "closed";

        when(messageRepository.findById(invalidMessageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.updateMessageStatus(invalidMessageId, newStatus);
        });
        verify(messageRepository, times(1)).findById(invalidMessageId);
        verify(messageRepository, times(0)).save(any(Message.class));
    }

    @Test
    public void testUpdateMessageStatus_NullStatus() {
        Long messageId = 1L;

        assertThrows(IllegalArgumentException.class, () -> {
            messageService.updateMessageStatus(messageId, null);
        });
        verify(messageRepository, times(0)).save(any(Message.class));
    }

    @Test
    public void testUpdateMessageStatus_InvalidStatus() {
        Long messageId = 1L;
        String invalidStatus = "invalid_status"; // Invalid status value

        Message existingMessage = new Message();
        existingMessage.setMessageId(messageId);
        existingMessage.setStatus("open");

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));

        assertThrows(IllegalArgumentException.class, () -> {
            messageService.updateMessageStatus(messageId, invalidStatus);
        });
        verify(messageRepository, times(0)).save(any(Message.class));
    }

    @Test
    public void testUpdateMessageStatus_NullMessageId() {
        String newStatus = "closed";

        assertThrows(IllegalArgumentException.class, () -> {
            messageService.updateMessageStatus(null, newStatus);
        });
        verify(messageRepository, times(0)).findById(any());
        verify(messageRepository, times(0)).save(any());
    }

    @Test
    public void testMapToResponseDTO_NullMessage() {
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.mapToResponseDTO(null);
        });
    }
}

