package com.song.selector.naver;

import com.song.selector.Selector;

public class MobileNaverSearchSelector implements Selector{
	
	private String priceSelector = "#root > div > div.nxKO8SM2-b > div > em";
	private String itemNameSelector = "#root > div > div.nxKO8SM2-b > h2";
	
	@Override
	public String getPriceSelector() {
		return priceSelector;
	}
	public void setPriceSelector(String priceSelector) {
		this.priceSelector = priceSelector;
	}
	@Override
	public String getItemNameSelector() {
		return itemNameSelector;
	}
	public void setItemNameSelector(String itemNameSelector) {
		this.itemNameSelector = itemNameSelector;
	}
	
	@Override
	public String toString() {
		return "MobileNaverSearchSelector [priceSelector=" + priceSelector + ", itemNameSelector=" + itemNameSelector
				+ "]";
	}
	
}