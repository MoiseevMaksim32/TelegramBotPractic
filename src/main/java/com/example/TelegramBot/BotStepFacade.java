package com.example.TelegramBot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class BotStepFacade {
    private final BotStep chainHead;

    public BotStepFacade(List<BotStep> steps) {
        if (steps.isEmpty()) {
            chainHead = new NoActiveBotStep();
        } else {
            for (int i = 0; i < steps.size(); i++) {
                var current = steps.get(i);
                var next = i < steps.size() - 1 ? steps.get(i + 1) : new NoActiveBotStep();
                current.setNext(next);
            }
            chainHead = steps.get(0);
        }
    }

    public void active(Update update) {
        chainHead.active(update);
    }
}
