package com.example.TelegramBot;

import com.example.TelegramBot.DTO.MessagesDTO;
import com.example.TelegramBot.DTO.UserDTO;
import com.example.TelegramBot.Model.Users;
import com.example.TelegramBot.Service.MessagesService;
import com.example.TelegramBot.Service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Order(0)
@Slf4j
public class StartBotStep implements BotStep {
    private BotStep next;
    private final CounterTelegramBot counterTelegramBot;
    private final UsersService usersService;
    private final MessagesService messagesService;

    // удалось решить проблему используя отложенную загрузку бина, чего я так сделал с самого начал не знаю
    @Autowired
    public StartBotStep(@Lazy CounterTelegramBot counterTelegramBot, UsersService usersService, MessagesService messagesService) {
        this.counterTelegramBot = counterTelegramBot;
        this.usersService = usersService;
        this.messagesService = messagesService;
    }

    @Override
    public void active(Update update) {
        if (update.getMessage().getText().equals("/start")) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();
            String userName = update.getMessage().getFrom().getFirstName();
            UserDTO dto = new UserDTO(userId, userName, messageText);
            Users user;
            MessagesDTO messagesDTO;
            log.info("Пользователь использовал \"старт\"");
            startBot(chatId, userName, userId);
            usersService.create(dto);
            user = usersService.readByIdTelegram(userId);
            log.info("Создан новый пользователь с id - " + user.getId());
            messagesDTO = new MessagesDTO(user.getId(), messageText);
            messagesService.create(messagesDTO);
        } else {
            next.active(update);
        }
    }

    @Override
    public void setNext(BotStep step) {
        this.next = step;
    }

    private void startBot(long chatId, String userName, long id_user) {
        log.info("Пользователь с id - " + id_user + " начал диалог с ботом");
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет, " + userName + " я собираю все ваши сообщения и отправляю их на локальный сервер");
        try {
            counterTelegramBot.execute(message);
            log.info("Запуск бота");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
