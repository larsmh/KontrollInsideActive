package com.insider.kontrollactive;

import java.util.ArrayList;

import com.insider.kontrollactiveModel.Globals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class ConnectionChangeReceiver extends BroadcastReceiver
{
@Override
	public void onReceive( Context context, Intent intent ){
		ArrayList<Email> list = Globals.emaiList;
		EmailPrep prepper = new EmailPrep(context);
	
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
    	NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(     ConnectivityManager.TYPE_MOBILE );
    	
    	if ( activeNetInfo != null ){
    		try {
    			prepper.setEmailListContent();
    			prepper.printNumberOfFiles();
				SendEmailTask task = new SendEmailTask(list);
	        	task.execute();
	        	Toast.makeText(context, "Email sendt!", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		Toast.makeText( context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
    	}
	
    	if( mobNetInfo != null ){
    		try {
    			prepper.printNumberOfFiles();
				prepper.setEmailListContent();
				SendEmailTask task = new SendEmailTask(list);
	        	task.execute();
	        	Toast.makeText(context, "Email sendt!", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		Toast.makeText( context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
    	}
  	}
}