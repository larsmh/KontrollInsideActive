package com.insider.kontrollactive;

import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.insider.kontrollactiveDatabase.DbAction;
import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.Date;
import com.insider.kontrollactiveModel.Globals;

import android.os.Environment;
import android.util.Log;

public class Email {

	String[] recipient;
	String subject ;
	String body;
	String json;
	String date;
	String path;
	String department;
	String type;
	
	Customer cust;
	File file; 
	Boolean hasAttachement;
	int custID;
	HttpClient client; 
	MultipartEntityBuilder builder;
	
	public Email() {
		
		client = new DefaultHttpClient();
		builder = MultipartEntityBuilder.create();
		hasAttachement = false;
		
		date = new Date().getDate();
	}
	
	public void setTo(String[] recipient){
		this.recipient = recipient;
		
		for (int i = 0; i < recipient.length; i++) {
			recipient[i] = "\"Recipient\":\""+recipient[i]+"\"";
		}
	}
	
	public void setSubject(String subject){
		this.subject ="\"Subject\":\""+subject+"\"";
	}
	
	
	public void setBody(String body){
		this.body = "\"Message\":\"Hei,\n"
				+body+"\"";
	}
	
	
	
	public void setDepartment(String deparment){
		this.department = "\"Department\":\""+Globals.user.getDepartment().toLowerCase()+"\"";
		
	}
	
	public void setCustomer(Customer cust){
		this.cust = cust;
	}
	
	public void setType(int type){
		this.type = "\"Type\":\""+type+"\"";
	}
	
	public void send(){
		Log.d("!!asd", this.department);
		DbAction dbAction = new DbAction();
		
		HttpResponse response;
        StringBody sb;
        try {
        	if(hasAttachement){
        	file = new File(path);
        	String json = "{"+recipient[0]+","+subject+","+body+","+department+","+type+"}";
        	StringEntity body = new StringEntity(json);
        	body.setContentType("application/json");
//        	HttpPost post = new HttpPost("http://192.168.221.48:8080/insider/email/");
        	HttpPost post = new HttpPost("https://kontroll.insider.no/insider/email/");
            post.setHeader("Authorization","Basic a29udHJvbGxpbnNpZGVhcHBAaW5zaWRlci5ubzp0MnJRZm0yZQ==");
            builder.addPart("file", new FileBody(file));
            builder.addTextBody("email",json,ContentType.APPLICATION_JSON);


            dbAction.registerJob(""+cust.getId(), ""+Globals.user.getId(), date);
            post.setEntity(builder.build());
            response = client.execute(post);
            Log.d("!!kunde", cust.getName());
            Log.d("!!kunde", Globals.user.getName()+" "+date);
            
        	}
        	else{
        		String json = "{"+recipient[0]+","+subject+","+body+","+department+","+type+"}";
        		StringEntity body = new StringEntity(json);
        		body.setContentType("application/json");

//        		HttpPost post = new HttpPost("http://192.168.221.48:8080/insider/email/");
        		
        		HttpPost post = new HttpPost("https://kontroll.insider.no/insider/email/");
        		post.setHeader("Authorization","Basic a29udHJvbGxpbnNpZGVhcHBAaW5zaWRlci5ubzp0MnJRZm0yZQ==");

        		builder.addTextBody("email",json,ContentType.APPLICATION_JSON);
        		dbAction.registerJob(""+cust.getId(), ""+Globals.user.getId(), date);
        		post.setEntity(builder.build());
                response = client.execute(post);
        	}

            
        } catch(Exception e) {
            e.printStackTrace();
           Log.d("Error", "Cannot Estabilish Connection");
        }
		
	}
	
	public void setAttachementFilePath(String path){
		this.path = path;
	}
	
	public void setAttachement(boolean attachement){
		hasAttachement = attachement;
	}
}
