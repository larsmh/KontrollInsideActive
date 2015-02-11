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
	
	public EmailPrep(ArrayList<Email> list, Customer cust, String date, Context context, String msg, String attachement, int type, int custID) {
		this.list = list;
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
    	String[] s = {email, date, msg, ""+cust.getId(), ""+Globals.user.getId()};
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
			
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setEmailListContent() throws Exception{
		String lines[] = {"","","","",""};
		String line;
		
		if( myDir.list().length != 0){
		for(File f: myDir.listFiles()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
				try {
					for (int j = 0; j < 5; j++) {
						lines[j] = br.readLine();
//						Log.d("!!", ""+lines[j]);
					}
					
				} catch (IOException e) {
					Log.d("!!", "Exception in setEmailListContent");
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			File dir = new File(Environment.getExternalStorageDirectory(), "insider_data");
			Email email = new Email();
			String[] toArr = {lines[0]}; 
            email.setTo(toArr);
            
            if(type == 3){
            	hasAttachement = true;
            	email.setAttachement(hasAttachement);
            	
            	email.setAttachementFilePath(attachement);
            	email.setSubject("Kvalitetsrapport "+cust.getName()+ " "+ lines[1]); 
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
            	email.setBody("Kjære kunde,\n"

            				+lines[2]+"\n"+
            			"Denne mailen ble sent: "+lines[1]); 		
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
