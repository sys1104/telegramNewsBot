package com.song.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface JohnBerDao {
	String getChatId(String chatId);
	
	int getPrice(HashMap<String, String> map);
	
	List<HashMap<String, Object>> getWishListAll();
	
	int getWishListCntByID(String chatId);
	
	List<HashMap<String, Object>> getWishListByID(String chatId);
	
	int regItem(HashMap<String, String> map);
	
	int regUser(String chatId);
	
	int delUser(String chatId);
	
	int updatePrice(HashMap<String, Object> map);
}
