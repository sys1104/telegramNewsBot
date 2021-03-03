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
        			.append("/가입 : 자동으로 사용자 등록\n")
        			.append("/사용법 : 사용방법 안내\n")
        			.append("/키워드목록 : 등록 키워드 목록 보기\n")
        			.append("/뉴스보기 : 현재시각 기준으로 등록된 키워드 뉴스 가져오기\n")
        			.append("/탈퇴 : 탈퇴");
        
        if ("/start".equals(message)) {
        	sendMessage(hello.append(command).toString());
        } else if ("/사용법".equals(message)) {
        	sendMessage("뉴스 키워드 등록하시면 매일 아침 뉴스가 발송됩니다.\n"
        			+ "@@@명령어 목록@@@\n"
        			+ "/등록 : 키워드 등록, ex) 등록 테슬라\n"
        			+ "/키워드목록 : 등록 키워드 목록 보기\n"
        			+ "/삭제 : 등록된 키워드 삭제 ex)/삭제 테슬라\n"
        			+ "/뉴스보기 : 현재시각으로 등록된 키워드 뉴스를 확인합니다.\n"
        			+ "/탈퇴 : 탈퇴 시 가입정보와 함께 등록된 키워드가 삭제됩니다.\n");
        } else if ("/가입".equals(message)) {
    			if (isRegistered()) {
    				sendMessage("이미 가입되었습니다.");
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
    				sendMessage("가입되지 않은 사용자입니다.");
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
					
					if(jsonArray.size()==0) {
						sb.append("등록된 키워드가 없습니다.\n");
					}else {
						sb.append("등록된 키워드는 아래와 같습니다.\n");
					}
					
					
					for ( int i = 0; i < jsonArray.size(); i++) {
						JsonObject jo = (JsonObject) jsonArray.get(i);
						newsKeyword = (i+1) + ". " + jo.get("KEYWORD").getAsString();
						sb.append(newsKeyword).append("\n");
					}
				}

				sendMessage(sb.toString());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if ("/뉴스보기".equals(message)) {
        	try {
				StringBuffer sb = new StringBuffer();
				String newsInfoJson = sendPost(chatId, "/getNewsInfoByID");
				
				if (newsInfoJson.length() <= 0) {
					sb.append("키워드 관련 없습니다");
				} else {

					String newsKeywordListJson = sendPost(chatId, "/getNewsKeyword");
					
					JsonParser jsonParser = new JsonParser();
					JsonArray jsonArray = (JsonArray) jsonParser.parse(newsInfoJson);
					
					JsonArray jsonKeywordArray = (JsonArray) jsonParser.parse(newsKeywordListJson);

					sb.append("키워드 관련 뉴스 목록\n");
					String newsTitle = "";
					String newsLink = "";
					for ( int i = 0; i < jsonArray.size(); i++) {
						JsonArray ja = (JsonArray) jsonArray.get(i);
						
						JsonObject jk = (JsonObject) jsonKeywordArray.get(i);
						String newsKeyword = jk.get("KEYWORD").getAsString();
						
						sb.append("=================================");
						sb.append("\n");
						sb.append("\n");
						sb.append(" < 키워드 : "+newsKeyword+" > ");
						sb.append("\n");
						
						if(ja.size()<=0) {
							sb.append("오늘은 뉴스가 없습니다.\n");
						}else {
							for (int j = 0; j < ja.size(); j++) {
								JsonObject jo = (JsonObject) ja.get(j);
								newsTitle = jo.get("title").getAsString();
								// [ ] 문자 하이퍼링크를 위한 예약어 -> <>로 치환
								newsTitle = newsTitle.replace("[", "<");
								newsTitle = newsTitle.replace("]", ">");
								newsLink = jo.get("link").getAsString();
								sb.append("\n");
								sb.append(j+1 + ". " + "[" + newsTitle + "]").append("(" + newsLink + ")");
								sb.append("\n");
							}							
						}

						if (i == jsonArray.size()-1) {
							sb.append("=================================");
						}
					}
				}

				sendMessage(sb.toString());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (message.startsWith("/등록") || message.startsWith("/삭제")) {
        	if ( !isRegistered() ) {
        		sendMessage("사용자 등록 후 처리가능합니다.");
        	} else {
        		try {
        			String replaceStr = "";
        			String targetAPI = "";  
        			if (message.startsWith("/등록")) {
        				replaceStr = "/등록";
        				targetAPI = "/regNewsKeyword"; 
        			} else if (message.startsWith("/삭제")) {
        				replaceStr = "/삭제";
        				targetAPI = "/delNewsKeyword";
        			}
        			
        			Map<String, Object> paramMap = new HashMap<>();
        			paramMap.put("chatId", this.chatId);
        			message = message.replaceAll(replaceStr, "");
        			message = message.replaceAll(" ", "");
        			paramMap.put("keyword", message);
        			
        			String rtnMsg = sendPostMap(paramMap, targetAPI); 
					
        			sendMessage(rtnMsg);
        			
            	} catch (Exception e) {
            		sendMessage("처리 도중 에러가 발생하였습니다. 개발자에게 문의하세요.");
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
//        return "1644646592:AAH6hYf1v3iHTa6gwUng_hXrdYXA7vHhJyM";
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
    	                new KeyboardButton("/가입"),
    	                new KeyboardButton("/사용법"),
    	                new KeyboardButton("/키워드목록"),
    	                new KeyboardButton("/뉴스보기")
    	        }
    	).resizeKeyboard(true);            	
    	
    	
    	SendMessage request = new SendMessage(this.chatId, message)
    	        .parseMode(ParseMode.Markdown)
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
