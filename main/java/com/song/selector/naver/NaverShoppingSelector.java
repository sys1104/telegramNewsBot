package com.song.selector.naver;

import com.song.selector.Selector;

public class NaverShoppingSelector implements Selector{
	
	private String priceSelector = "#content > div._2-I30XS1lA > div._29bBAzpH0u > fieldset > div._28YSyWTW-S > div > div > strong > span._7bB3O2y55c";
	private String itemNameSelector = "#content > div._2-I30XS1lA > div._29bBAzpH0u > fieldset > div._28YSyWTW-S > h3";
	
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
