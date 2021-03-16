package com.song.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.song.dao.NewsAPIDAO;
import com.song.exception.RegDupException;
import com.song.exception.RegLimitOverException;
import com.song.service.BotService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class NewsAPIController {
	@Autowired
	NewsAPIDAO dao;
	@Autowired
	BotService service;
	
	@RequestMapping("/getChatId")
	
	public String getChatId(HttpServletRequest request) {
		String paramChatId = request.getParameter("chatId");
		String chatId = "";
		try {
			chatId = service.getChatId(paramChatId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString());
		}
		return chatId;
	}
	@RequestMapping("/regUser")
	public void regChatId(HttpServletRequest request) {
		String paramChatId = request.getParameter("chatId");
		int result = -1;
		try {
			result = service.regUser(paramChatId);
			log.info(result +  "건 등록완료" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString());
		}
	}
	@RequestMapping("/delUser")
	public void delUser(HttpServletRequest request) {
		String paramChatId = request.getParameter("chatId");
		int result = -1;
		try {
			result = service.delUser(paramChatId);
			log.info(result +  "건 삭제완료" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString());
		}
	}
	@RequestMapping("/delNewsKeyword")
	public String delNewsKeyword(@RequestParam HashMap<String, String> paramMap) {
		int result = -1;
		String rtnMsg = "";
		try {
			result = service.delNewsKeyword(paramMap);
			log.info(result +  "건 삭제완료" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString());
		}
		if (result > 0) {
			rtnMsg = String.valueOf(result) + "건 삭제되었습니다.";
		} else {
			rtnMsg = "삭제할 키워드가 없습니다.";
		}
		return rtnMsg;
	}	
	@RequestMapping("/regNewsKeyword")
	public String regNewsKeyword(@RequestParam HashMap<String, String> paramMap) {
		int insertedCnt = 0;
		String rtnMsg = ""; 
		try {
			insertedCnt = service.regNewsKeyword(paramMap);
			rtnMsg = insertedCnt + "건 입력되었습니다";
		} catch (SQLException sqle) {
			log.error(sqle.toString());
			rtnMsg = "DB입력 중 에러가 발생하였습니다. 개발자에게 문의하세요";
		} catch (RegLimitOverException rloe) {
			log.error(rloe.toString());
			rtnMsg = rloe.getMessage();
		} catch (RegDupException rde) {
			rtnMsg = rde.getMessage();
		} catch (Exception e) {
			log.error(e.toString());
			rtnMsg = "입력 중 에러가 발생하였습니다.";
		}
		return rtnMsg;
	}
	@RequestMapping("/getNewsKeyword")
	public String getNewsKeyword(HttpServletRequest request) {

		String paramChatId = request.getParameter("chatId");
		String newsKeywordJson = "";
		List<HashMap<String, Object>> newsKeywordList = new ArrayList<>();
		try {
			if ("".equals(paramChatId)) {
				newsKeywordList = service.getNewsKeywordAll();
			} else {
				newsKeywordList = service.getNewsKeywordByID(paramChatId);
			}
			log.info(newsKeywordList.size() +  "건 ");
		} catch (SQLException e) {
			log.error(e.toString());
		}
		
		Gson gson = new Gson();
		newsKeywordJson = gson.toJson(newsKeywordList);
		return newsKeywordJson;
	}
	@RequestMapping("/getNewsInfo")
	public String getNewsInfo(HttpServletRequest request) {

		String paramChatId = request.getParameter("chatId");
		String newsKeywordJson = "";
		
		try {
			if ("".equals(paramChatId)) {
				newsKeywordJson = service.getNewsInfoAllUser();
			} else {
				newsKeywordJson = service.getNewsInfoByID(paramChatId);
			}
		} catch (SQLException e) {
			log.error(e.toString());
		}

		return newsKeywordJson;
	}	
	
	
	
}
