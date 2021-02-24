package com.song;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.song.controller.BotListener;

import lombok.extern.slf4j.Slf4j;


//@MapperScan(value={"com.song.dao"})
@SpringBootApplication
@ComponentScan
@EnableScheduling
@EnableCaching
@Slf4j
public class SpringWebServiceApplication {
	
	public static void main(String[] args) {
	    ApiContextInitializer.init();
	    TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
	    BotListener bot =  new BotListener();
	    
        try {
            telegramBotsApi.registerBot(bot);           
        } catch (TelegramApiException e) {
            log.error(e.toString());
        }
		SpringApplication.run(SpringWebServiceApplication.class, args);
	}

}
