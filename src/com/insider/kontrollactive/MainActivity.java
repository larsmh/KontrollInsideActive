package com.insider.kontrollactive;

import java.util.ArrayList;
import java.util.Calendar;
import com.insider.kontrollactiveDatabase.CustomerListDB;
import com.insider.kontrollactiveDatabase.DbAction;
import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.Date;
import com.insider.kontrollactiveModel.Globals;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	private AutoCompleteTextView custSelect;
	private EditText msgText;
	private Button regButton, msgButton;
	public ArrayList<Email> emailList;
	EmailPrep prepper;
	Customer cust;
	String attachement;
	private String date, msg;
	int type = 0;
	DbAction db;
	
	
    protected void onCreate(Bundle savedInstanceState) {
        
		Globals.custDB = new CustomerListDB(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.in_logo);
        attachement ="";
        emailList = Globals.emaiList;
        custSelect = (AutoCompleteTextView) findViewById(R.id.custselect);
        
        IntentFilter filter = new IntentFilter(Intent.ACTION_DEFAULT);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        this.registerReceiver(this.receiver, filter);  
        
        final ActionBarActivity a = this;
        
        custSelect.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(Globals.custList!=null){
				ArrayAdapter<Customer> adapter = new ArrayAdapter<Customer>(a, android.R.layout.simple_list_item_single_choice, Globals.custList);
				custSelect.setAdapter(adapter);
				}				
			}
        });
        custSelect.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
			}
		});
        custSelect.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode==KeyEvent.KEYCODE_DEL)
					custSelect.setText("");
				return false;
			}
		});
        
        Button qualityButton = (Button)findViewById(R.id.qualitybutton);
        if(Globals.user.getAdmin()==true){
        	qualityButton.setVisibility(View.VISIBLE);
        }
        
        msgText = (EditText)findViewById(R.id.msgText);
        regButton = (Button)findViewById(R.id.regbutton);
        msgButton = (Button)findViewById(R.id.messagebutton);
        
    }
	public void showMessage(View view){
		if(msgText.isShown()){
			setMsgInvisible();
		}
		else{
			setMsgVisible();
		}
	}
	private void setMsgVisible(){
		msgText.setVisibility(View.VISIBLE);
		regButton.setText(R.string.sendMsg);
		msgButton.setText(R.string.abort);
		msgText.requestFocus();
		InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
	}
	private void setMsgInvisible(){
		msgText.setVisibility(View.INVISIBLE);
		regButton.setText(R.string.regvask);
		msgButton.setText(R.string.message);
		InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
	}
	
	
    public void register(View view){
    	
    	cust = getCustomer(custSelect.getText().toString());
    	if(cust==null){
    		Toast.makeText(getApplicationContext(), 
    				"Ingen gyldig kunde valgt. Velg kunde pï¿½ nytt!",
         			Toast.LENGTH_LONG).show();
    		return;
    	}
    	String title="Registrering av oppdrag";
    	String message="Er du sikker på at du vil registrere dette oppdraget?";
    	if(msgText.isShown()){
    		title="Sending av avviksmelding";
    		message="Er du sikker på at du vil sende avviksmeldingen?";
    		type = 1;
    	}	
    	new AlertDialog.Builder(this)
    	.setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) { 
        		try {
					finishRegistration();
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
        })
        .show();
    }
    private void finishRegistration() throws Exception{
    	    	
    	//Sending email
    	date = new Date().getDate();
    	msg=msgText.getText().toString();
    	Log.d("!!msg", "Message: "+msg);
    	if(msgText.isShown()){
    		msg=msgText.getText().toString();
    		}
    	else{
	    	db = new DbAction();
	    	db.registerJob(cust.getName(), date);
    	}
    	Log.d("!!inne i finish","lol");
    	
    	EmailGenerator gen = new EmailGenerator(this,cust,date,msg,emailList,attachement,type);
    	gen.sendEmail();
    	msgText.setText("");
    	setMsgInvisible();
    	custSelect.setText("");
    }
    
    
    private void updateList(){
    	ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	if (connec != null && 
                (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) || 
                (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){
    		db = new DbAction();
    		db.retrieveCustomers();
    	}
    	else{
    		Globals.custList = new ArrayList<Customer>();
    		Globals.custDB.getData();
    	}
    }
    
    private Customer getCustomer(String name){
    	for(Customer cust : Globals.custList){
    		if(name.equals(cust.getName()))
    			return cust;
    	}
    	return null;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	int id = item.getItemId();
        if (id == R.id.action_log_out) {
        	new AlertDialog.Builder(this)
        	.setTitle("Utlogging")
            .setMessage("Er du sikker pï¿½ at du vil logge ut?")
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int which) { 
            		logout();
            	}
            })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                    // do nothing
                }
            })
            .show();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
    	moveTaskToBack(true);
    }
    
    protected void onResume (){
    	super.onResume();
    	updateList();
    }
    private void logout(){
    	SharedPreferences userData = getSharedPreferences("UserFile", 0);
		SharedPreferences.Editor editor = userData.edit();
		editor.putString("phonenr", "null");
		editor.putString("password", "null");
		editor.putString("dept", "null");
		editor.putString("name", "null");
		editor.putBoolean("admin", false);
		editor.commit();
		
		Globals.user = null;
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
    }
    
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) { 
//        	
//        	 EmailPrep prepper = new EmailPrep(emailList, cust, date, context, msg,attachement);
//        	 ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//             if (connec != null && 
//                 (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) || 
//                 (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){ 
//             	
//            	 	try {
//						prepper.setEmailListContent();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//             	
//            	 	new SendEmailTask(emailList).execute();
//                
//            	if(emailList.size() > 0)
//            		Toast.makeText(getApplicationContext(), "Email sendt!", Toast.LENGTH_SHORT).show();
//            	
//             	
//             }
//        	
//        	
//        }
//      }; 
    
    public void quality(View v){
    	cust = getCustomer(custSelect.getText().toString());
    	
    	QualityDialog qualityDialog = new QualityDialog(cust,emailList);
		qualityDialog.show(getSupportFragmentManager(), "Quality Report");

    }
}
