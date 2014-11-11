package com.insider.kontrollactiveModel;

import java.util.Calendar;

public class Date {

	String date;
	public Date() {
		Calendar c = Calendar.getInstance();
    	date=c.get(Calendar.DATE)+"."+(c.get(Calendar.MONTH)+1)+"."+c.get(Calendar.YEAR)+"-"
    			+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
	}
	
	public String getDate(){
		return date;
	}
}
