package com.github.eseoa.sensualgamebot.config;


import com.github.eseoa.sensualgamebot.service.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//чтобы наш бот заработал его надо инициализировать
@Component
//@Slf4j
public class BotInit {

    @Autowired
    TelegramBotService bot;
    // не знаю что за анотация
    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
//            log.error("Error occurred" + e.getMessage());
        }
    }
}
