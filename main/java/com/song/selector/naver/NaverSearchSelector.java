package com.song.selector.naver;

import com.song.selector.Selector;

public class NaverSearchSelector implements Selector{
	
	
	private String priceSelector = "#__next > div > div.style_container__3iYev > div.style_inner__1Eo2z > div.style_content_wrap__2VTVx > div.style_content__36DCX > div > div.summary_info_area__3XT5U > div.lowestPrice_price_area__OkxBK > div.lowestPrice_low_price__fByaG > em";
	private String itemNameSelector = "#__next > div > div.style_container__3iYev > div.style_inner__1Eo2z > div.top_summary_title__15yAr > h2 text";
	
	public String getPriceSelector() {
		return priceSelector;
	}
	public void setPriceSelector(String priceSelector) {
		this.priceSelector = priceSelector;
	}
	public String getItemNameSelector() {
		return itemNameSelector;
	}
	public void setItemNameSelector(String itemNameSelector) {
		this.itemNameSelector = itemNameSelector;
	}
	
	@Override
	public String toString() {
		return "NaverSearchSelector [priceSelector=" + priceSelector + ", itemNameSelector=" + itemNameSelector + "]";
	}
	
}
