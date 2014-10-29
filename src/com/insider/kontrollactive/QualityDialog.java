package com.insider.kontrollactive;

import java.util.ArrayList;

import com.insider.kontrollactiveModel.Customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

public class QualityDialog extends DialogFragment{

	Customer cust;
	ArrayList<Mail> emailList;
	public QualityDialog(Customer cust, ArrayList<Mail> emailList) {
		this.emailList = emailList;
		this.cust = cust;
		
	}
	
	  @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setItems(R.array.quality_report_types, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d("!!", "which = "+which);
			    	Intent intent = new Intent(getActivity(), QualityReportActivity.class);
			    	intent.putExtra("choice", ""+which);
			    	intent.putExtra("customerObject", cust);
			    	startActivity(intent);
			    	
			    	if(which == 5){
			    		new standardQualityReportDialog();
			    	}
				}
			});
	      
	        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	  }
	 
	  public class standardQualityReportDialog  extends DialogFragment{
		  
		  public Dialog onCreateDialog(Bundle savedInstanceState){
			
			  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		      builder.setItems(R.array.standard_quality_report_types, new DialogInterface.OnClickListener() {
			  
		    	  @Override
					public void onClick(DialogInterface dialog, int which) {
						
		    		  	if(which == 0) which = 6;
		    		  	else which = 7;
		    		  
				    	Intent intent = new Intent(getActivity(), QualityReportActivity.class);
				    	intent.putExtra("choice", ""+which);
				    	intent.putExtra("customerObject", cust);
				    	startActivity(intent);
				    	
				    	
					}
				});
		    	  
		    	  
			  return builder.create();
			  
		  }
		  
	  }
}