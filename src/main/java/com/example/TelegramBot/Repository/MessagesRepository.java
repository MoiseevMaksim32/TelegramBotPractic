package com.example.TelegramBot.Repository;

import com.example.TelegramBot.Model.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesRepository extends JpaRepository<Messages,Integer>{
}
