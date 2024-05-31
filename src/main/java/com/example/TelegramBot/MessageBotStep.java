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
@Order(1)
@Slf4j
public class MessageBotStep implements BotStep {
    private BotStep next;
    private final CounterTelegramBot counterTelegramBot;
    private final UsersService usersService;
    private final MessagesService messagesService;

    @Autowired
    public MessageBotStep(@Lazy CounterTelegramBot counterTelegramBot, UsersService usersService, MessagesService messagesService) {
        this.counterTelegramBot = counterTelegramBot;
        this.usersService = usersService;
        this.messagesService = messagesService;

    }

    @Override
    public void active(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getFrom().getFirstName();
        UserDTO dto = new UserDTO(userId, userName, messageText);
        Users user;
        MessagesDTO messagesDTO;
        user = usersService.readByIdTelegram(dto.getIdTelegram());
        user.setLast_message_at(dto.getLast_message_at());
        writeMessage(chatId, userName, userId);
        usersService.update(user);
        messagesDTO = new MessagesDTO(user.getId(), messageText);
        messagesService.create(messagesDTO);
    }

    @Override
    public void setNext(BotStep step) {
        this.next = step;
    }

    private void writeMessage(long chatId, String userName, long userId) {
        log.info("Пользователь с id - " + userId + " написал новое сообщение");
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Мы приняли ваше сообщение, " + userName);
        try {
            counterTelegramBot.execute(message);
            log.info("Анализ записи");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
