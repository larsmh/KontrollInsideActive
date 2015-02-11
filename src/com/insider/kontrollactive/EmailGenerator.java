package com.insider.kontrollactive;

import java.util.ArrayList;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	int userID;
	
	public EmailGenerator(Context context, Customer cust, String date, String msg, ArrayList<Email> emailList, String attachement, int type, int userID) {
		this.cust = cust;
		this.date = date;
		this.msg = msg;
		this.emailList = emailList;
		this.context = context;
		this.attachement = attachement;
		this.type = type;
		this.userID = userID;
		
	}
	
	public void sendEmail() throws Exception{
    	EmailPrep prepper = new EmailPrep(emailList, cust, date, context, msg, attachement,type, userID);
    	
    	prepper.createLocalEmail();
    	
//    	prepper.printNumberOfFiles();
    	ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec != null && 
           (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) || 
           (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){
        	
        	prepper.setEmailListContent();	
        	SendEmailTask task = new SendEmailTask(emailList);
        	task.execute();
        	Toast.makeText(context, "Email sendt!", Toast.LENGTH_SHORT).show();

        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                 connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {            
                //Not connected.
                Toast.makeText(context.getApplicationContext(), "Ingen tilgang til internett.", Toast.LENGTH_LONG).show();
        }
    }
}
