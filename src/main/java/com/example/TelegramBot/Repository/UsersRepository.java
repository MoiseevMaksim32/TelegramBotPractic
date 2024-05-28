package com.example.TelegramBot.Repository;

import com.example.TelegramBot.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    @Query(value = "select * from users where id_telegram = ?1", nativeQuery = true)
    public Users findByIdTelegram(Long id);
}
