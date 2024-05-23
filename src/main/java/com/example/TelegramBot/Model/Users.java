package com.example.TelegramBot.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    private long id;
    @Column(name = "users_name", nullable = false)
    private String users_name;
    @Column(name = "last_message_at",nullable = false)
    private String last_message_at;
}
