package com.insider.kontrollactiveDatabase;

import android.util.Log;

import com.insider.kontrollactiveModel.Globals;


public class DbAction {

	String url ="http://78.91.8.239:8080/insider/";

	String user = "franang_admin";
	String pw = "tranduil123";
	String query;
	
	public void retrieveCustomers(){	
		query = "SELECT * FROM customer WHERE department='"+Globals.user.getDepartment()+"'";
		new RetrieveCustomers().execute(url, Globals.user.getDepartment());
	}
	
	public void registerJob(String customer, String date){
		/*query="INSERT INTO jobs(customer, employee, date) VALUES('"+customer+"', '"
				+Globals.user.getDepartment()+"', '"+date+"')";
		Log.d("!!!!", query);*/
		new RegisterJob().execute(url, customer, Globals.user.getPhonenr(), date);
	}
	
	public void retrieveUser(String phonenr){
		query = "SELECT * FROM employee WHERE phonenr='"+phonenr+"'";
		//new RetrieveUser().execute(url, user, pw, query);
		new RetrieveUser().execute(url, phonenr);
	}
	
}
