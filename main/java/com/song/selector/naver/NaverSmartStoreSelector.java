package com.song.selector.naver;

import com.song.selector.Selector;

public class NaverSmartStoreSelector implements Selector{
	
	private String priceSelector = "#wrap > div > div.prd_detail_basic > div.info > form > fieldset > div._copyable > dl > dd > div.area_cost > strong > span.thm";
	private String itemNameSelector = "#wrap > div > div.prd_detail_basic > div.info > form > fieldset > div._copyable > dl > dt > strong";
	
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
		return "NaverShoppingSelector [priceSelector=" + priceSelector + ", itemNameSelector=" + itemNameSelector + "]";
	}
	
}
