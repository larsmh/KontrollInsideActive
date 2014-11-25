package com.insider.kontrollactiveModel;

import java.util.Calendar;

public class Date {

	String date;
	public Date() {
		Calendar c = Calendar.getInstance();
		date=c.get(Calendar.YEAR)+"-"+String.format("%02d",(c.get(Calendar.MONTH)+1))+"-"+String.format("%02d",c.get(Calendar.DATE))+"_"
				+String.format("%02d",c.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d",c.get(Calendar.MINUTE));
	}
	
	public String getDate(){
		return date;
	}
}
