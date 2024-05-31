package com.example.TelegramBot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
@Slf4j
public class NoActiveBotStep implements BotStep{
    @Override
    public void active(Update update) {
        log.error("Нет действия для данного действия пользователя");
    }

    @Override
    public void setNext(BotStep step) {

    }
}
