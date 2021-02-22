package com.song.crawler;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.song.factory.SelectorFactory;
import com.song.selector.Selector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NaverCrawler {
	
	public HashMap<String, Object> getItemInfoMap(String url) {
		HashMap<String, Object> itemInfo = new HashMap<>();
		url = mUrlToPcUrl(url);
		log.debug("PC url : " + url);
		try {
			SelectorFactory factory = new SelectorFactory();
			Selector selector = factory.createSelector(url);
			log.debug("selectorName : " + selector.getClass().getName());
			String priceSelector = selector.getPriceSelector();
			String itemNameSelector = selector.getItemNameSelector();
			Document doc = Jsoup.connect(url).get();
			Elements itemPrices = doc.select(priceSelector);
			Elements itemNames = doc.select(itemNameSelector);

			for (Element itemName : itemNames) {
				log.debug("상품명 : " + itemName.text());
				itemInfo.put("itemName", itemName.text());
			}
			for (Element itemPrice : itemPrices) {
				log.debug("가격 : " + itemPrice.html());
				itemInfo.put("itemPrice", itemPrice.html());
			}
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			throw e;
		} catch (IOException ie) {
			log.error(ie.toString());
		} catch (NullPointerException ne) {
			log.debug("상품명/가격 추출 실패, 빈값으로 입력 됨");
			log.error(ne.getMessage());
		}
		return itemInfo;
	}
	//msearch일 경우 상품코드 추출하여 pcURL로 변경
	public String mUrlToPcUrl(String url) {
 		String nvMid = "";
 		int nvMidEndIdx = 0;
		if (url.contains("msearch.shopping.naver")) {
			StringBuilder sb = new StringBuilder();
			//nvMid = 네이버 검색 상품코드 파라미터
			int nvMidStartIdx = url.indexOf("catalog/");
			//boolean isURLStartWithCatalog = nvMidStartIdx ? ;
			if (nvMidStartIdx != -1) {
				nvMidStartIdx = nvMidStartIdx + "catalog/".length();
				nvMidEndIdx = url.indexOf("?");
			} else if (nvMidStartIdx == -1) {
				nvMidStartIdx = url.indexOf("nv_mid=");
				if (nvMidStartIdx != -1) {
					nvMidStartIdx = nvMidStartIdx + "nv_mid=".length();
					nvMidEndIdx = url.indexOf("&query");
				}
			}
			
			nvMid = url.substring(nvMidStartIdx, nvMidEndIdx);
			url = "https://search.shopping.naver.com/detail/detail.nhn?nv_mid=";
			sb.append(url);
			sb.append(nvMid);
			url = sb.toString();
		}
		return url;
	}
}
