package com.insider.kontrollactiveDatabase;

import android.util.Log;

import com.insider.kontrollactiveModel.Globals;


public class DbAction {
	String url ="https://kontroll.insider.no/insider/";
	
	public void retrieveCustomers(){	
		new RetrieveCustomers().execute(url, Globals.user.getDepartment());
	}
	
	public void registerJob(String customer, String date){
		new RegisterJob().execute(url, customer, Globals.user.getPhonenr(), date);
	}
	
	public void retrieveUser(String phonenr){
		new RetrieveUser().execute(url, phonenr);
	}
	
}
