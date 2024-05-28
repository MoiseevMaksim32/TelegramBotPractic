package com.example.TelegramBot;

import com.example.TelegramBot.DTO.MessagesDTO;
import com.example.TelegramBot.DTO.UserDTO;
import com.example.TelegramBot.Model.Users;
import com.example.TelegramBot.Service.DailyDomainsService;
import com.example.TelegramBot.Service.MessagesService;
import com.example.TelegramBot.Service.UsersService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Slf4j
@Component
public class CounterTelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    final CloseableHttpClient httpClient = HttpClients.createDefault();
    @Autowired
    private UsersService usersService;
    @Autowired
    private MessagesService messagesService;
    @Autowired
    private DailyDomainsService dailyDomainsService;

    public CounterTelegramBot(BotConfig botConfig) {
        this.config = botConfig;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (update.getMessage().hasText()) {
            log.info("Пользователь написал сообщение");
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();
            String userName = update.getMessage().getFrom().getFirstName();
            botAnswerUtils(messageText, chatId, userId, userName);
        }
    }

    private void botAnswerUtils(String mess, long chatId, long id_user, String userName) {
        UserDTO dto = new UserDTO(id_user, userName, mess);
        Users user;
        MessagesDTO messagesDTO;
        // К сожелению не смог реализовать шаблон проектирования цепочки отвественности
        switch (mess) {
            case "/start":
                log.info("Пользователь использовал \"старт\"");
                startBot(chatId, userName, id_user);
                if (usersService.readByIdTelegram(id_user) == null) createUser(dto);
                user = usersService.readByIdTelegram(id_user);
                messagesDTO = new MessagesDTO(user.getId(), mess);
                messagesService.create(messagesDTO);
                break;
            default:
                user = usersService.readByIdTelegram(dto.getIdTelegram());
                user.setLast_message_at(dto.getLast_message_at());
                writeMessage(chatId, userName, id_user);
                usersService.update(usersService.readByIdTelegram(dto.getIdTelegram()));
                messagesDTO = new MessagesDTO(user.getId(), mess);
                messagesService.create(messagesDTO);
                break;
        }
    }

    private void startBot(long chatId, String userName, long id_user) {
        log.info("Пользователь с id - " + id_user + " начал диалог с ботом");
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет, " + userName + " я собираю все ваши сообщения и отправляю их на локальный сервер");
        try {
            execute(message);
            log.info("Запуск бота");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void writeMessage(long chatId, String userName, long userId) {
        log.info("Пользователь с id - " + userId + " написал новое сообщение");
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Мы приняли ваше сообщение, " + userName);
        try {
            execute(message);
            log.info("Анализ записи");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void createUser(UserDTO dto) {
        try {
            usersService.create(dto);
            log.info("Создан новый пользователь с id - " + dto.getIdTelegram());
        } catch (Exception e) {
            Users user = usersService.readByIdTelegram(dto.getIdTelegram());
            user.setLast_message_at(dto.getLast_message_at());
            usersService.update(user);
            log.info("Пользовательс с id был обновлён: " + user.getId());
        }
    }
}
