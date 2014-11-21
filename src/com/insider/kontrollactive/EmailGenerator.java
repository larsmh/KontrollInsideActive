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
	ArrayList<Email> emailList;
	Context context;
	String attachement;
	int type;
	public EmailGenerator(Context context, Customer cust, String date, String msg, ArrayList<Email> emailList, String attachement, int type) {
		this.cust = cust;
		this.date = date;
		this.msg = msg;
		this.emailList = emailList;
		this.context = context;
		this.attachement = attachement;
		this.type = type;
		
	}
	
	public void sendEmail() throws Exception{
    	EmailPrep prepper = new EmailPrep(emailList, cust, date, context, msg, attachement,type);
    	
    	prepper.createLocalEmail();
//    	Log.d("!!sjekker", attachement);
    	
//    	prepper.printNumberOfFiles();
    	
//    	Log.d("!!inne i sendEmail",cust.getEmail()+date);
    	 ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
       if (connec != null && 
           (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) || 
           (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){ 
        	Log.d("!!inne i sendEmail","Online!!");
        	
        	prepper.setEmailListContent();
        	
        	Log.d("Lum", "Number of emails in list: "+emailList.size());
				
        	SendEmailTask task = new SendEmailTask(emailList);
        	task.execute();
            
        	Toast.makeText(context, "Email sendt!", Toast.LENGTH_SHORT).show();
        	
        	
        	
        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                 connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {            
                //Not connected.
        	Log.d("!!inne i sendEmail","ikke connected");
        	
        	Log.d("Lum", "Number of emails in list: "+emailList.size());
        		
                Toast.makeText(context.getApplicationContext(), "Ingen tilgang til internett.", Toast.LENGTH_LONG).show();
        }
        
//        Log.d("!!inne i sendEmail?", "woot?");
//        prepper.deleteAllFiles();
    }
}
