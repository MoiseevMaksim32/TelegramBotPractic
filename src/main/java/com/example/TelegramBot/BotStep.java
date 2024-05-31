package com.example.TelegramBot;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotStep {
    void active(Update update);

    void setNext(BotStep step);
}
