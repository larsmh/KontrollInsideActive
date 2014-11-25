package com.insider.kontrollactiveDatabase;

import android.util.Log;

import com.insider.kontrollactiveModel.Globals;


public class DbAction {
	String url ="https://kontroll.insider.no/insider/";
//	String url ="http://192.168.221.48:8080/insider/";
	public void retrieveCustomers(){	
		new RetrieveCustomers().execute(url, Globals.user.getDepartment());
	}
	
	public void registerJob(String customer, String user, String date){
		new RegisterJob().execute(url, customer, user, date);
	}
	
	public void retrieveUser(String phonenr){
		new RetrieveUser().execute(url, phonenr);
	}
	
}
