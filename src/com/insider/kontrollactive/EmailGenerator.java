package com.insider.kontrollactive;

import java.util.ArrayList;
import java.util.Date;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.insider.kontrollactiveModel.Customer;

public class EmailGenerator {

	Customer cust;
	String date;
	String msg;
	ArrayList<Mail> emailList;
	Context context;
	boolean attachement;
	public EmailGenerator(Context context, Customer cust, String date, String msg, ArrayList<Mail> emailList, boolean attachement) {
		this.cust = cust;
		this.date = date;
		this.msg = msg;
		this.emailList = emailList;
		this.context = context;
		this.attachement = attachement;
	}
	
	public void sendEmail() throws Exception{
    	Log.d("!!inne i sendEmail",msg);
    	
    	EmailPrep prepper = new EmailPrep(emailList, cust, date, context, msg, attachement);
    	
    	prepper.createLocalEmail();
    	prepper.printNumberOfFiles();
    	
        ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec != null && 
            (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) || 
            (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){ 
        	
        	prepper.setEmailListContent();
        	
        	Log.d("Lum", "Number of emails in list: "+emailList.size());
				
        	SendEmailTask task = new SendEmailTask(emailList);
        	task.execute();
            
        	Toast.makeText(context, "Email sendt!", Toast.LENGTH_SHORT).show();
        	
        	
        	
        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                 connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {            
                //Not connected.    
        	Log.d("Lum", "Number of emails in list: "+emailList.size());
        		
                Toast.makeText(context.getApplicationContext(), "Ingen tilgang til internett.", Toast.LENGTH_LONG).show();
        }
//        prepper.deleteAllFiles();
    }
}
