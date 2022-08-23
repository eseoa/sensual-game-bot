package com.github.eseoa.sensualgamebot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//конфиг бота, тут все понятно
@Configuration
@PropertySource("application.yaml")
@Data
public class BotConfig {

    @Value(value = "${telegram.bot.username}")
    private String botUserName;
    @Value(value = "${telegram.bot.token}")
    private String token;

}
