package com.example.TelegramBot.Service;

import com.example.TelegramBot.DTO.MessagesDTO;
import com.example.TelegramBot.Model.Messages;
import com.example.TelegramBot.Repository.MessagesRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessagesService {
    private MessagesRepository messagesRepository;
    private UsersService usersService;

    public Messages create(MessagesDTO dto){
        Messages message = Messages.builder().Users(usersService.readById(dto.getUsers_id()))
                .messages_text(dto.getMessages_text())
                .build();
        return messagesRepository.save(message);
    }

    public List<Messages> readAll(){
        return messagesRepository.findAll();
    }

    public Messages update(Messages message){
        return messagesRepository.save(message);
    }

    public Messages readById(Integer id){
        return messagesRepository.findById(id).orElseThrow(() ->
                new RuntimeException("В таблицы Roles нет значения с таким: "+id));
    }

    public void delete(Integer id){
        messagesRepository.deleteById(id);
    }
}
