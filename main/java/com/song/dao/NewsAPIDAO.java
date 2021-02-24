package com.song.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface NewsAPIDAO {
	String getChatId(String chatId);
	
	int getPrice(HashMap<String, String> map);
	
	List<HashMap<String, Object>> getAllNews();
	
	int getWishListCntByID(String chatId);
	
	List<HashMap<String, Object>> getNewsKeywordByID(String chatId);
	
	int regNewsKeyword(HashMap<String, String> map);
	
	int regUser(String chatId);
	
	int delUser(String chatId);
	
	int delNewsKeyword(HashMap<String, Object> map);
}
