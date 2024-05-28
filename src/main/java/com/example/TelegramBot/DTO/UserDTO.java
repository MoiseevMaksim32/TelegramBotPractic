package com.example.TelegramBot.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private long idTelegram;
    private String users_name;
    private String last_message_at;
}
