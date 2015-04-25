package com.insider.kontrollactive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.Globals;

public class EmailPrep {

	ArrayList<Email> list;
	String date, msg;
	Customer cust;
	Context context;
	File myDir;
	int custID;
	String attachement;
	boolean hasAttachement;
	int type;
	
	public EmailPrep(Context context) {
		this.context = context;
		list = Globals.emaiList;
		myDir = context.getDir("myDir", Context.MODE_PRIVATE);
	}
	
	public EmailPrep(Customer cust, String date, Context context, String msg, String attachement, int type, int custID) {
		list = Globals.emaiList;
		this.date = date;
		this.cust = cust;
		this.context = context;
		this.msg = msg;
		this.attachement = attachement;
		this.type = type;
		myDir = context.getDir("myDir", Context.MODE_PRIVATE);
		this.custID = custID;
	}
	
	public void createLocalEmail(){

		String email = cust.getEmail();
    	String name = cust.getName();
    	String attachement = this.attachement;
    	if(this.attachement == "") attachement = "no attachement";
    	int type = this.type;
    	String[] s = {email, date, msg, ""+cust.getId(), name, attachement, ""+type, ""+Globals.user.getId()};
    	File file;    	
		file = new File(myDir.getAbsolutePath(),name+myDir.list().length+".txt");
		//Create a new file.
		try {
			
			file.createNewFile();
		} catch (IOException e) {
			Log.d("!!", "Excpetion in createLocalEmail");
			e.printStackTrace();
		}
		//write to the file.
		BufferedWriter writer;
		
		try {
			
			writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
			
				
				writer.write(s[0]);
				writer.newLine();
				writer.write(s[1]);
				writer.newLine();
				writer.write(s[2]);
				writer.newLine();
				writer.write(s[3]);
				writer.newLine();
				writer.write(s[4]);
				writer.newLine();
				writer.write(s[5]);
				writer.newLine();
				writer.write(s[6]);
				writer.newLine();
				writer.write(s[7]);
				writer.newLine();
				
			
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setEmailListContent() throws Exception{
		String lines[] = {"","","","","","","",""};
		String line;
		
		if( myDir.list().length != 0){
		for(File f: myDir.listFiles()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
				try {
					for (int j = 0; j < 8; j++) {
						lines[j] = br.readLine();
						Log.d("!!Lumm", ""+lines[j]);
					}
					
				} catch (IOException e) {
					Log.d("!!", "Exception in setEmailListContent");
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int type = Integer.parseInt(lines[6]);
			String custEmail = lines[0];
			String custName = lines[4];
			this.attachement = lines[5];
			
			int custId = Integer.parseInt(lines[3]);
		
			File dir = new File(Environment.getExternalStorageDirectory(), "insider_data");
			
			Customer cust = new Customer(custId, custName, custEmail, "");
			Log.d("!!Cust",""+cust.getId()+", "+cust.getName()+", "+cust.getEmail());
			Email email = new Email();
			String[] toArr = {lines[0]}; 
            email.setTo(toArr);
            
            if(type == 3){
            	hasAttachement = true;
            	email.setAttachement(hasAttachement);
            	
            	email.setAttachementFilePath(attachement);
            	email.setSubject("Kvalitetsrapport "+lines[4]+ " "+ lines[1]); 
            	email.setBody("Vedlagt ligger kvalitetsrapport, som ble utført "+lines[1]+"\n"
            			+ "\n"+
            			"mvh\n"+
            			"Insider Facility Services AS");
            	email.setDepartment(cust.getDepartment());
            	email.setType(type);
            	email.setCustomer(cust);
            	
            }
            
            if(type == 1){
            	email.setAttachement(hasAttachement);
            	email.setSubject("Renhold ikke mulig på grunn av avvik. "+cust.getName()+ " "+ lines[1]);
            	email.setBody("Følgende avviksmelding ble registrert ved renhold den "+lines[1]+":\n\n"
            				+lines[2]+"\n\n"+"mvh\n"+
            			"Insider Facility Services AS"); 		
            	email.setDepartment(cust.getDepartment());
            	email.setType(type);
            	email.setCustomer(cust);
            }
            
            
            
            if(type == 0) {
            	email.setAttachement(hasAttachement);
            	email.setSubject("Kvittering for utført renhold, "+cust.getName()+ " "+ lines[1]); 
            	email.setBody("Insider har nå utført dagens renhold.\n" +
            			"\n"+
            			"mvh\n"+
            			"Insider Facility Services AS");
            	email.setDepartment(cust.getDepartment());
            	email.setType(type);
            	email.setCustomer(cust);
            	
            }
					
			list.add(email);
			Log.d("Lumm test",""+Globals.emaiList+" , "+list);
			f.delete();
		}
	}
	}
	
	public ArrayList<Email> getEmailList(){
		
		return list;
	}
	
	public void printNumberOfFiles(){
		Log.d("Lumm","Number of files: "+myDir.list().length);
		
	}
	
	public void deleteAllFiles(){
		for(File f: myDir.listFiles()) 
			  f.delete();
		
	}
	
}
