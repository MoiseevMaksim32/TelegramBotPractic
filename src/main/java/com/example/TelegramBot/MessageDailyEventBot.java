package com.example.TelegramBot;

import com.example.TelegramBot.Model.Users;
import com.example.TelegramBot.Service.DailyDomainsService;
import com.example.TelegramBot.Service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class MessageDailyEventBot {
    @Autowired
    private CounterTelegramBot bot;
    @Autowired
    private UsersService usersService;
    @Autowired
    private DailyDomainsService dailyDomainsService;

    public void messageUsers() {
        List<Users> usersList = usersService.readAll();
        for (var i : usersList) {
            SendMessage message = new SendMessage();
            message.setChatId(i.getIdTelegram());
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            message.setText(localDateTime.format(formatter) + ". Собранно " + dailyDomainsService.countItems() + " доменов");
            try {
                bot.execute(message);
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке пользователю с id - " + i.getId() + "сообщения о количестве собранных доменов: " + e.getMessage());
            }
        }
    }
}
