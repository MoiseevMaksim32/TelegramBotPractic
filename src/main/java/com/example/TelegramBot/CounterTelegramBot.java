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
    private final BotStepFacade botStepFacade;
    @Autowired
    public CounterTelegramBot(BotConfig botConfig,BotStepFacade botStepFacade) {
        this.botStepFacade = botStepFacade;
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
            botStepFacade.active(update);
        }
    }
}
