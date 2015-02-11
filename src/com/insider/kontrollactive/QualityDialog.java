package com.insider.kontrollactive;

import java.util.ArrayList;

import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.User;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class QualityDialog extends DialogFragment{
	Intent intent;
	Customer cust;
	User user;
	ArrayList<Email> emailList;
	public QualityDialog(Customer cust, User user, ArrayList<Email> emailList) {
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
					
					switch (which) {
					case 0:
						intent = new Intent(getActivity(), com.insider.kontrollactiveReports.StandardQualityReport.class);
						intent.putExtra("choice", ""+which);
						intent.putExtra("customerObject", cust);
						
						
						startActivity(intent);
						break;
					case 1:
						intent = new Intent(getActivity(), com.insider.kontrollactiveReports.OppstartQualityReport.class);
						intent.putExtra("choice", ""+which);
						intent.putExtra("customerObject", cust);
						
						startActivity(intent);
						break;
					case 2:
						intent = new Intent(getActivity(), com.insider.kontrollactiveReports.BarnehageQualityReport.class);
						intent.putExtra("choice", ""+which);
						intent.putExtra("customerObject", cust);
						
						startActivity(intent);
						break;
					case 3:
						intent = new Intent(getActivity(), com.insider.kontrollactiveReports.ButikkQualityReport.class);
						intent.putExtra("choice", ""+which);
						intent.putExtra("customerObject", cust);
						
						startActivity(intent);
						break;
					case 4:
						intent = new Intent(getActivity(), com.insider.kontrollactiveReports.EiendomsdriftQualityReport.class);
						intent.putExtra("choice", ""+which);
						intent.putExtra("customerObject", cust);
						
						startActivity(intent);
						break;
					case 5:
						intent = new Intent(getActivity(), com.insider.kontrollactiveReports.HelsebyggQualityReport.class);
						intent.putExtra("choice", ""+which);
						intent.putExtra("customerObject", cust);
						
						startActivity(intent);
						break;
					case 6:
						intent = new Intent(getActivity(), com.insider.kontrollactiveReports.NaeringsbyggQualityReport.class);
						intent.putExtra("choice", ""+which);
						intent.putExtra("customerObject", cust);
						
						startActivity(intent);
						break;
					default:
						break;
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
	  
	    
}