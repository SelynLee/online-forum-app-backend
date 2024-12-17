package com.beaconfire.messages_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO {
    private Long userId;
    private String email;
    private String subject;
    private String message;
}

