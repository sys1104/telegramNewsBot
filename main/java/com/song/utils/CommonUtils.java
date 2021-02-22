package com.song.utils;

public final class CommonUtils {

	public static String addMoneyComma (String money) {
		int moneyInt = Integer.parseInt(removeComma(money));
		money = String.format("%,d", moneyInt);
		return money;
	}
	
	public static String removeComma (String str) {
		str = str.replace(",", "");
		return str;
	}
	
	public static String rawUrlToATag (String url) {
		StringBuffer sb = new StringBuffer();
		sb.append("<a href=\"");
		sb.append(url);
		sb.append("\">이동</a>");
		return sb.toString();
		
	}
}