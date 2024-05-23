package com.example.TelegramBot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DailyEventBot implements Runnable{
    @Autowired
    CounterTelegramBot bot;

    @SneakyThrows
    @Override
    public void run() {
        log.info("Запус процесса сбора данных запросом c внешнего сервера");
        bot.getDaily_domains();
        bot.messageUsers();
    }
}
