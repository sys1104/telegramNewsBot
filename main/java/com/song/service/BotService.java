package com.song.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.song.dao.NewsAPIDAO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class BotService {
	@Autowired
	private NewsAPIDAO dao;

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

	public int regNewsKeyword(HashMap<String, String> map) throws Exception, SQLException {
		int regItemCnt = getWishListCntByID(map.get("chatId"));
		
		if (regItemCnt > REG_ITEM_LIMIT) {
			throw new Exception("상품 등록은 최대 " + String.valueOf(REG_ITEM_LIMIT) + "개 까지만 가능합니다.");
		}
		return dao.regNewsKeyword(map);
	}

	public int getPrice(HashMap<String, String> map) throws SQLException {
		return dao.getPrice(map);
	}

	public List<HashMap<String, Object>> getAllNews() throws SQLException {
		return dao.getAllNews();
	}
	
	public int getWishListCntByID(String chatId) throws SQLException {
		return dao.getWishListCntByID(chatId);
	}
	
	public List<HashMap<String, Object>> getWishListByID(String chatId) throws SQLException {
		return dao.getNewsKeywordByID(chatId);
	}
	
	// 가격변동 시 Update
	public List<HashMap<String, Object>> getUpdateList(List<HashMap<String, Object>> wishList) throws SQLException {
		
		List<HashMap<String, Object>> updateList = new ArrayList<HashMap<String, Object>>();

		return updateList;
	}
	
//	
//	// 가격변동 시 Update 및 메시지 발송
//	public int updatePrice(HashMap<String, Object> map) throws SQLException {
//		StringBuffer sb = new StringBuffer();
//		String itemPrice = CommonUtils.removeComma(map.get("itemPrice").toString());
//		String oldPrice = map.get("oldPrice").toString();
//		String itemName = map.get("itemName").toString();
//		String url = map.get("url").toString();
//		sb.append("@@가격변동알림@@" + "\n상품명 : ").append(itemName)
//		   .append("\n이전가격 : ").append(CommonUtils.addMoneyComma(oldPrice))
//		   .append("\n현재가격 : ").append(CommonUtils.addMoneyComma(itemPrice))
//		   .append("\n링크이동 : ").append(CommonUtils.rawUrlToATag(url));
//		String message = sb.toString();
//		sendMessage(message, map.get("chatId").toString());
//		return dao.updatePrice(map);
//	}

}