package com.insider.kontrollactiveReports;

import com.insider.kontrollactive.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SignatureDialog extends DialogFragment {
	
	SignatureView signView;
	
	public SignatureDialog(Context context) {
		// TODO Auto-generated constructor stub
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		
		return builder.create();
		
	}
	
	
}
