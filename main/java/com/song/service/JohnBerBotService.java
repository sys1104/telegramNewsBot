package com.song.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.song.crawler.NaverCrawler;
import com.song.dao.JohnBerDao;
import com.song.utils.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class JohnBerBotService {
	@Autowired
	private JohnBerDao dao;
	private NaverCrawler crawler = new NaverCrawler();

	final int REG_ITEM_LIMIT = 5;
	
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

	public int regItem(HashMap<String, String> map) throws Exception, SQLException {
		int regItemCnt = getWishListCntByID(map.get("chatId"));
		
		if (regItemCnt > REG_ITEM_LIMIT) {
			throw new Exception("상품 등록은 최대 " + String.valueOf(REG_ITEM_LIMIT) + "개 까지만 가능합니다.");
		}
		return dao.regItem(map);
	}

	public int getPrice(HashMap<String, String> map) throws SQLException {
		return dao.getPrice(map);
	}

	public List<HashMap<String, Object>> getWishListAll() throws SQLException {
		return dao.getWishListAll();
	}
	
	
	public int getWishListCntByID(String chatId) throws SQLException {
		return dao.getWishListCntByID(chatId);
	}
	
	public List<HashMap<String, Object>> getWishListByID(String chatId) throws SQLException {
		return dao.getWishListByID(chatId);
	}
	
	// 가격변동 시 Update
	public List<HashMap<String, Object>> getUpdateList(List<HashMap<String, Object>> wishList) throws SQLException {
		
		List<HashMap<String, Object>> updateList = new ArrayList<HashMap<String, Object>>();

		for (HashMap<String, Object> map : wishList) {
			String seq = map.get("SEQ").toString();
			String url = map.get("URL").toString();
			String oldPrice = map.get("ITEM_PRICE").toString();
			String chatId = map.get("CHAT_ID").toString();
			HashMap<String, Object> itemInfo = crawler.getItemInfoMap(url);
			itemInfo.put("seq", seq);
			itemInfo.put("chatId", chatId);
			itemInfo.put("oldPrice", oldPrice);
			String newPrice = CommonUtils.removeComma(itemInfo.get("itemPrice").toString());
			itemInfo.remove("itemPrice");
			itemInfo.put("itemPrice", newPrice);
			itemInfo.put("url", url);

			if (oldPrice.equals(newPrice)) {
				continue;
			} else {
				log.debug("updateListAdded : " + itemInfo.toString());
				updateList.add(itemInfo);
			}
		}
		return updateList;
	}
	
	
	// 가격변동 시 Update 및 메시지 발송
	public int updatePrice(HashMap<String, Object> map) throws SQLException {
		StringBuffer sb = new StringBuffer();
		String itemPrice = CommonUtils.removeComma(map.get("itemPrice").toString());
		String oldPrice = map.get("oldPrice").toString();
		String itemName = map.get("itemName").toString();
		String url = map.get("url").toString();
		sb.append("@@가격변동알림@@" + "\n상품명 : ").append(itemName)
		   .append("\n이전가격 : ").append(CommonUtils.addMoneyComma(oldPrice))
		   .append("\n현재가격 : ").append(CommonUtils.addMoneyComma(itemPrice))
		   .append("\n링크이동 : ").append(CommonUtils.rawUrlToATag(url));
		String message = sb.toString();
		sendMessage(message, map.get("chatId").toString());
		return dao.updatePrice(map);
	}

	public String sendMessage(String message, String chatId) {
		TelegramBot bot = new TelegramBot("952633662:AAGDJOld3g9M691pvvMM8ULmF7oRzYhkvR4");
		SendMessage request = new SendMessage(chatId, message)
				.parseMode(ParseMode.HTML)
				.disableWebPagePreview(true)
				.disableNotification(false);
		SendResponse sendResponse = bot.execute(request);
		boolean ok = sendResponse.isOk();
		Message responseMessage = sendResponse.message();
		// log.debug( "responseMessage : " + responseMessage);
		return String.valueOf(ok);
	}

}