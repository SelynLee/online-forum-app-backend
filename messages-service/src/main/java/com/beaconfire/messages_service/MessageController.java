package com.beaconfire.messages_service;

import com.beaconfire.messages_service.dto.MessageRequestDTO;
import com.beaconfire.messages_service.dto.MessageResponseDTO;
import com.beaconfire.messages_service.dto.MessageStatusUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createMessage(@RequestBody MessageRequestDTO requestDTO) {
        MessageResponseDTO responseDTO = messageService.createMessage(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDTO>> getAllMessages() {
        List<MessageResponseDTO> messages = messageService.getAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> updateMessageStatus(
            @PathVariable Long id, @RequestBody String status) {
        MessageResponseDTO responseDTO = messageService.updateMessageStatus(id, status);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}

