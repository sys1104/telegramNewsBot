package com.song.crawler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class NewsCrawler {
	
	public JsonArray getNewsInfoByKeyword(String newsKeyword) throws IOException, Exception {
		
		final int MAX_NEWS_COUNT = 5;
		final String urlPrefix = "https://news.google.com/rss/search?q=";
		final String urlSuffix = "+when:1d&hl=ko&gl=KR&ceid=KR:ko";		
		String url = "";

		url = urlPrefix + newsKeyword + urlSuffix;
		JsonArray newsJsonArray = new JsonArray();
		
		try {
			Document doc = Jsoup.connect(url).get();
			Elements titles = doc.getElementsByTag("title");
			Elements links  = doc.getElementsByTag("link");
			for (int i = 0; i < (MAX_NEWS_COUNT+1); i++) {
				if ( i == 0 ) continue;
				JsonObject news = new JsonObject();
				news.addProperty("title", titles.get(i).text());
				news.addProperty("link", links.get(i).text());
				newsJsonArray.add(news);
			}
		} catch (IOException ioe) {
			throw new IOException("newsKeyword를 확인하세요. keyword : " + newsKeyword);
		} catch (IndexOutOfBoundsException iobe) {
			return newsJsonArray;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("NewsKeywordCrawling 중 알수없는 예외 발생");
			
		}

		return newsJsonArray;
		
	}
}
