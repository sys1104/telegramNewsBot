package com.song.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.song.dao.NewsAPIDAO;
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
			e.printStackTrace();
		}
	}
	@RequestMapping("/regItem")
	public String regItem(@RequestParam HashMap<String, String> paramMap) {
		int result = -1;
		String rtnMsg = "";
		try {
			result = service.regNewsKeyword(paramMap);
			log.info(result +  "건 등록완료" );
			rtnMsg = result + "건 등록완료";
		} catch (Exception e) {
			log.error(e.toString());
			rtnMsg = e.getMessage(); 
		}
		return rtnMsg;
	}
	@RequestMapping("/getWishList")
	//@Cacheable(value="wishList")
	public String getWishList(HttpServletRequest request) {

		String paramChatId = request.getParameter("chatId");
		String wishListJson = "";
		List<HashMap<String, Object>> itemList = new ArrayList<HashMap<String, Object>>();
		try {
			if ("".equals(paramChatId)) {
				itemList = service.getAllNews();
			} else {
				itemList = service.getWishListByID(paramChatId);
			}
			log.info(itemList.size() +  "건 ");
		} catch (SQLException e) {
			log.error(e.toString());
		}
		
		Gson gson = new Gson();
		wishListJson = gson.toJson(itemList);
		return wishListJson;
	}
	
	@RequestMapping("/updatePrice")
	public void updatePrice(HttpServletRequest request) {
		List<HashMap<String, Object>> updateList = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> wishList;
		String chatId = request.getParameter("chatId");
		
		try {
			if ( chatId == null || "".equals(chatId)) {
				wishList = service.getAllNews();
			} else {
				wishList = service.getWishListByID(chatId);
			}
			updateList = service.getUpdateList(wishList);
			log.info( "업데이트 대상 리스트 : " + updateList.size() +  "건 " );
			if (updateList.size() <= 0) {
				log.info("가격 변동 상품 없음");
			} else {
				for (HashMap<String, Object> map : updateList) {
//					log.info(service.updatePrice(map) + "건 업데이트 완료");
				}
			}
		} catch (SQLException e) {
			log.error(e.toString());
		}
	}
	
}
