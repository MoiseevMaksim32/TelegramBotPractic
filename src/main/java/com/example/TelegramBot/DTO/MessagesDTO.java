package com.example.TelegramBot.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessagesDTO {
    private long users_id;
    private String messages_text;
}
