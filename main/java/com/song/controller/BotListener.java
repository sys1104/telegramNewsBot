package com.song.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BotListener extends TelegramLongPollingBot {
	private String chatId;
	
    @Override
    public void onUpdateReceived(Update arg0) {
        // TODO
//        log.debug(arg0.getMessage().getFrom().getId()); //get ID 는 user id
    	log.debug(arg0.getMessage().getFrom().getLastName()); //get ID 는 user id
        log.debug(arg0.getMessage().getFrom().getFirstName()); //get ID 는 user id
        log.debug(arg0.getMessage().getChatId().toString());  // 채팅방의 ID
        log.info(arg0.getMessage().getText());  // 받은 TEXT
        this.chatId = String.valueOf(arg0.getMessage().getChatId());
        String message = arg0.getMessage().getText();
        
        StringBuffer hello = new StringBuffer("\"안녕하세요 존버봇입니다.\\n \"");
        StringBuffer command = new StringBuffer();
        command.append("@@@명령어 목록@@@\n")
        			.append("/등록 : 자동으로 사용자 등록\n")
        			.append("/사용법 : 사용방법 안내\n")
        			.append("/상품목록 : 찜한 상품 목록 보기\n")
        			.append("/가격갱신 : 가격변동확인(변동없으면 미발송)\n")
        			.append("/탈퇴 : 탈퇴");
        
        if ("/start".equals(message)) {
        	sendMessage(hello.append(command).toString());
        } else if ("/사용법".equals(message)) {
        	sendMessage("사용자 등록 후 네이버 쇼핑의 특정상품 URL을 입력하면 자동으로 원하는 상품이 등록됩니다.");
        } else if ("/가입".equals(message)) {
    			if (isRegistered()) {
    				sendMessage("이미 가입된 사용자입니다.");
    			} else {
    				try {
    					sendPost(chatId, "/regUser");
    				} catch (Exception e) {
    					log.debug(e.toString());
    				}
    				sendMessage("가입완료되었습니다.");
    			}
        } else if ("/탈퇴".equals(message)) {
    		try {
    			if ( !isRegistered() ) {
    				sendMessage("사용자 정보가 존재하지 않습니다.");
    			} else {
    				sendPost(chatId, "/delUser");
    				sendMessage("탈퇴완료되었습니다.");    				
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
        } else if ("/키워드목록".equals(message)) {
        	try {
				StringBuffer sb = new StringBuffer();
				String newsKeyword = "";
				
				String newsKeywordListJson = sendPost(chatId, "/getNewsKeyword");
				
				if (newsKeywordListJson.length() <= 0) {
					sb.append("등록된 키워드가 없습니다");
				} else {
					JsonParser jsonParser = new JsonParser();
					JsonArray jsonArray = (JsonArray) jsonParser.parse(newsKeywordListJson);
					sb.append("등록된 키워드는 다음과 같습니다\n\n\n");
					
					for ( int i = 0; i < jsonArray.size(); i++) {
						JsonObject jo = (JsonObject) jsonArray.get(i);
						newsKeyword = jo.get("ITEM_PRICE").getAsString();
						sb.append(newsKeyword).append("\n");
					}
				}

				sendMessage(sb.toString());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (message.startsWith("/등록")) {
        	if ( !isRegistered() ) {
        		sendMessage("사용자 등록 후 상품 등록 가능합니다.");
        	} else {
        		try {
        			Map<String, Object> paramMap = new HashMap<>();
        			paramMap.put("chatId", this.chatId);
        			message = message.replaceAll("/등록", "");
        			message = message.replaceAll(" ", "");
        			paramMap.put("keyword", message);
        			
        			String rtnMsg = sendPostMap(paramMap, "/regNewsKeyword"); 
					
        			sendMessage(rtnMsg);
        			
            	} catch (IllegalArgumentException iae) {
            		sendMessage(iae.getMessage());
            		log.error( "URL = " + message + "에러메시지 : " + iae.getMessage());
            	} catch (Exception e) {
            		sendMessage("상품등록 도중 에러가 발생하였습니다. 개발자에게 문의하세요.");
            		log.error(e.toString());
            	}
        	}
        } else {
        	sendMessage("지원하지 않는 명령어입니다.\n" + command.toString());
        }
        //log.debug(arg0.getMessage().getReplyToMessage().getText());  // bot이 물어 본 받은 TEXT 사용자
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "johnberBot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "952633662:AAGDJOld3g9M691pvvMM8ULmF7oRzYhkvR4";
    }
    
    public String sendPost(String parameter, String method) throws Exception {
    	String rtnData = "";
    	String baseURL = "http://localhost:8080";
    	String URLMethod = method;
        URL url = new URL(baseURL.concat(URLMethod)); // 호출할 url
        Map<String,Object> params = new LinkedHashMap<>(); // 파라미터 세팅
        params.put("chatId", parameter);
 
        StringBuilder postData = new StringBuilder();
        for(Map.Entry<String,Object> param : params.entrySet()) {
            if(postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
 
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes); // POST 호출
 
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
 
        String inputLine;
        while((inputLine = in.readLine()) != null) { // response 출력
        	rtnData = inputLine;
            log.debug(inputLine);
        }
 
        in.close();
        return rtnData;
    }
    public String sendPostMap(Map<String, Object> paramMap, String method) throws Exception {
    	String rtnData = "";
    	String baseURL = "http://localhost:8080";
    	String URLMethod = method;
        URL url = new URL(baseURL.concat(URLMethod)); // API 호출 url + method
//        Map<String,Object> params = new LinkedHashMap<>(); // 파라미터 세팅
//        params.put("chatId", paramMap.get("chatId"));
//        params.put("keywords", paramMap.get("keywords"));

 
        StringBuilder postData = new StringBuilder();
        for(Map.Entry<String,Object> param : paramMap.entrySet()) {
            if(postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
 
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes); // POST 호출x
 
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
 
        String inputLine;
        while((inputLine = in.readLine()) != null) { // response 출력
        	rtnData = inputLine;
            log.debug(inputLine);
        }
 
        in.close();
        return rtnData;
    }
    public String sendMessage(String message) {
    	TelegramBot bot = new TelegramBot(getBotToken());

//    	InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
//    	        new InlineKeyboardButton[] {
//    	                new InlineKeyboardButton("test1").url("www.google.com"),
//    	                new InlineKeyboardButton("test2").callbackData("callback_data"),
//    	                new InlineKeyboardButton("test3!").switchInlineQuery("switch_inline_query")
//    	        });    	

    	Keyboard keyboard = new ReplyKeyboardMarkup(
    	        new KeyboardButton[] {
    	                new KeyboardButton("/등록"),
    	                new KeyboardButton("/사용법"),
    	                new KeyboardButton("/상품목록"),
    	                new KeyboardButton("/가격갱신"),
    	        }
    	).resizeKeyboard(true);            	
    	
    	
    	SendMessage request = new SendMessage(this.chatId, message)
    	        .parseMode(ParseMode.HTML)
    	        .disableWebPagePreview(true)
    	        .disableNotification(false)
    	        .replyMarkup(keyboard);                                 

    	SendResponse sendResponse = bot.execute(request);
    	boolean ok = sendResponse.isOk();
    	Message responseMessage = sendResponse.message();
    	log.debug( "responseMessage : " + responseMessage);
    	
    	return String.valueOf(ok);
    }
    @Scheduled(cron = "0 0 9,19 * * *")
    public void scheduler() {
    	try {
			sendPost("", "/updatePrice");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public boolean isRegistered() {
    	try {
    		String rtnChatId = sendPost(this.chatId, "/getChatId");
    		if (rtnChatId.length() > 1) {
    			return true;
    		} else {
    			return false;
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return true;
    }
}
