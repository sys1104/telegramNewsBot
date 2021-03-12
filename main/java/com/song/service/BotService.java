package com.song.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.song.crawler.NewsCrawler;
import com.song.dao.NewsAPIDAO;
import com.song.exception.RegDupException;
import com.song.exception.RegLimitOverException;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class BotService {
	@Autowired
	private NewsAPIDAO dao;
	@Autowired
	private NewsCrawler newsCrawler;
	final int REG_KEYWORD_LIMIT = 5;
	
	public String getChatId(String chatId) throws SQLException {
		log.debug("Service.getChatId");
		return dao.getChatId(chatId);
	}
	public int regUser(String chatId) throws SQLException {
		return dao.regUser(chatId);
	}

	public int delUser(String chatId) throws SQLException {
		return dao.delUser(chatId);
	}

	public int regNewsKeyword(HashMap<String, String> map) throws RegLimitOverException, SQLException, Exception {
		int regNewsKeywordCnt = getNewsKeywordCountById(map.get("chatId"));
		int dupKeywordCnt = duplKeywordCnt(map);
		
		if (regNewsKeywordCnt >= REG_KEYWORD_LIMIT) {
			throw new RegLimitOverException("키워드 등록은 최대 " + String.valueOf(REG_KEYWORD_LIMIT) + "개 까지만 가능합니다.");
		}else if(dupKeywordCnt > 0) {
			throw new RegDupException("이미 등록된 키워드 입니다.");
		}
		return dao.regNewsKeyword(map);
	}

	public int delNewsKeyword(HashMap<String, String> map) throws RegLimitOverException, SQLException, Exception {
		return dao.delNewsKeyword(map);
	}	
	
	public List<HashMap<String, Object>> getNewsKeywordAll() throws SQLException {
		return dao.getNewsKeywordAll();
	}
	
	public int getNewsKeywordCountById(String chatId) throws SQLException {
		return dao.getNewsKeywordCountById(chatId);
	}
	
	public int duplKeywordCnt(HashMap<String, String> paramMap) throws SQLException {
		return dao.duplKeywordCnt(paramMap);
	}
	
	public List<HashMap<String, Object>> getNewsKeywordByID(String chatId) throws SQLException {
		return dao.getNewsKeywordByID(chatId);
	}
	
	public String getNewsInfoAllUser() throws SQLException {
		String newsInfoAllUser = "";
		JsonArray newsJsonArrayAllUser = new JsonArray();
		List<String> allChatIdList = dao.getAllChatId();
		//전체 사용자 뉴스정보 조회
		for (String chatId : allChatIdList) {
			JsonObject newsJsonInfoByChatId = getNewsInfoJsonByID(chatId);
			newsJsonArrayAllUser.add(newsJsonInfoByChatId);
		}
		newsInfoAllUser = newsJsonArrayAllUser.toString();
		
		return newsInfoAllUser;
	}
	public JsonObject getNewsInfoJsonByID(String chatId) throws SQLException {
		List<HashMap<String, Object>> keywords = dao.getNewsKeywordByID(chatId);
		JsonObject newsInfoByChatId = new JsonObject();
		newsInfoByChatId.addProperty("chatId", chatId);
		JsonArray newsInfoArray = new JsonArray();
		for (HashMap<String, Object> map : keywords) {
			String newsKeyword = map.get("KEYWORD").toString();
			try {
				JsonObject newsInfoByKeyword = new JsonObject();
				JsonArray newsList = newsCrawler.getNewsInfoByKeyword(newsKeyword);
				newsInfoByKeyword.addProperty("keyword", newsKeyword);
				newsInfoByKeyword.add("newsList", newsList);
				newsInfoArray.add(newsInfoByKeyword);
			} catch (NullPointerException npe) {
				log.error(npe.getMessage());
				log.error(npe.toString());
			} catch (IOException ioe) {
				log.error(ioe.getMessage());
				log.error(ioe.toString());
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			
		}
		newsInfoByChatId.add("newsInfo", newsInfoArray);
		
		return newsInfoByChatId;
	}	
	
	public String getNewsInfoByID(String chatId) throws SQLException {
		List<HashMap<String, Object>> keywords = dao.getNewsKeywordByID(chatId);
		JsonArray newsJsonArray = new JsonArray();
		for (HashMap<String, Object> map : keywords) {
			String newsKeyword = map.get("KEYWORD").toString();
			try {
				JsonArray newsList = newsCrawler.getNewsInfoByKeyword(newsKeyword);
				newsJsonArray.add(newsList);
			} catch (NullPointerException npe) {
				log.error(npe.getMessage());
				log.error(npe.toString());
			} catch (IOException ioe) {
				log.error(ioe.getMessage());
				log.error(ioe.toString());
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		String newsInfoJson = "";
		newsInfoJson = newsJsonArray.toString();
		return newsInfoJson;
	}

}