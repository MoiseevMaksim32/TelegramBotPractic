package com.example.TelegramBot.Service;

import com.example.TelegramBot.Model.Users;
import com.example.TelegramBot.Repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsersService {
    private UsersRepository usersRepository;

    public Users create(Users model){
        Users user = Users.builder().
                id(model.getId()).
                last_message_at(model.getLast_message_at()).
                users_name(model.getUsers_name())
                .build();
        return usersRepository.save(user);
    }

    public List<Users> readAll(){
        return usersRepository.findAll();
    }

    public Users update(Users user){
        return usersRepository.save(user);
    }

    public Users readById(Long id){
        return usersRepository.findById(id).orElseThrow(() ->
                new RuntimeException("В таблицы Roles нет значения с таким: "+id));
    }

    public void delete(Long id){
        usersRepository.deleteById(id);
    }
}
