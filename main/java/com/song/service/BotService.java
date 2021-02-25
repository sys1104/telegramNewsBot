package com.song.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	final int REG_KEYWORD_LIMIT = 3;
	
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
	
	// 가격변동 시 Update
	public List<HashMap<String, Object>> getUpdateList(List<HashMap<String, Object>> wishList) throws SQLException {
		
		List<HashMap<String, Object>> updateList = new ArrayList<HashMap<String, Object>>();

		return updateList;
	}

}