package com.song.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.song.controller.JohnberBotListener;

public class MessageService {
	JohnberBotListener chatBot = new JohnberBotListener();
    public String sendMessage(String message, String chatId) {
    	TelegramBot bot = new TelegramBot(chatBot.getBotToken()); 
    	SendMessage request = new SendMessage(chatId, message)
    	        .parseMode(ParseMode.HTML)
    	        .disableWebPagePreview(true)
    	        .disableNotification(false);
    	SendResponse sendResponse = bot.execute(request);
    	boolean ok = sendResponse.isOk();
    	Message responseMessage = sendResponse.message();
    	//System.out.println( "responseMessage : " + responseMessage);
    	return String.valueOf(ok);
    }
}
