package com.insider.kontrollactive;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.Globals;

public class EmailGenerator {
	
	ArrayList<Email> list;
	Customer cust;
	String date;
	String msg;
	Context context;
	String attachement;
	int type;
	int userID;
	
	public EmailGenerator(Context context, Customer cust, String date, String msg, ArrayList<Email> emailList, String attachement, int type, int userID) {
		list = Globals.emaiList;
		this.cust = cust;
		this.date = date;
		this.msg = msg;
		this.context = context;
		this.attachement = attachement;
		this.type = type;
		this.userID = userID;
		
	}
	
	public void sendEmail() throws Exception{
    	EmailPrep prepper = new EmailPrep(cust, date, context, msg, attachement,type, userID);
    	
    	prepper.createLocalEmail();
    	
    	prepper.printNumberOfFiles();
    	
    	ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec != null && 
           (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) || 
           (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){
        	
        	prepper.setEmailListContent();	
        	SendEmailTask task = new SendEmailTask(list);
        	task.execute();
        	Toast.makeText(context, "Email sendt!", Toast.LENGTH_SHORT).show();

        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                 connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {            
                //Not connected.
        	Toast.makeText(context.getApplicationContext(), "Ingen tilgang til internett.", Toast.LENGTH_LONG).show();
        }
    }
}
