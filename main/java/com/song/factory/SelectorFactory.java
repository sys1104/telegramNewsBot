package com.song.factory;

import com.song.selector.Selector;
import com.song.selector.naver.MobileNaverSearchSelector;
import com.song.selector.naver.NaverSearchSelector;
import com.song.selector.naver.NaverShoppingSelector;
import com.song.selector.naver.NaverSmartStoreSelector;

public class SelectorFactory extends Factory {

	@Override
	public Selector createSelector(String url) {
		
		if (url.contains("msearch.shopping.naver")) return new MobileNaverSearchSelector();
		else if (url.contains("search.shopping.naver")) return new NaverSearchSelector();
		//Mobile도 가능(Mobile URL접속 -> PC URL 자동 리다이렉트)
		else if (url.contains("shopping.naver")) return new NaverShoppingSelector();
		//Mobile도 가능(Mobile URL접속 -> PC URL 자동 리다이렉트)
		else if (url.contains("smartstore.naver")) return new NaverSmartStoreSelector();
		else throw new IllegalArgumentException("지원하지 않는 쇼핑몰 또는 URL입니다. :)");
	}

}