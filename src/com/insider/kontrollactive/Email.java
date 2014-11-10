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

import android.os.Environment;
import android.util.Log;

public class Email {

	String[] recipient;
	String subject ;
	String body;
	String json;
	String date;
	String path;
	File file; 
	Boolean hasAttachement;
	
	HttpClient client; 
	MultipartEntityBuilder builder;
	
	public Email() {
		
		
		client = new DefaultHttpClient();
		builder = MultipartEntityBuilder.create();
		hasAttachement = false;
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
	
	public void setDate(String date){
		this.date = "\"Date\":\""+date+"\"";
	}
	public void send(){
		
		HttpResponse response;
        StringBody sb;
        
        try {
        	if(hasAttachement){
        		Log.d("!!Jeg skal sende ting", "funker det?");
        	file = new File(path);
        	String json = "{"+recipient[0]+","+subject+","+body+"}";
        	StringEntity body = new StringEntity(json);
        	body.setContentType("application/json");
//        	StringBody body = new StringBody(json, ContentType.APPLICATION_JSON);
            HttpPost post = new HttpPost("http://192.168.1.4:8080/api/email/");
            builder.addPart("file", new FileBody(file));
            builder.addTextBody("email",json,ContentType.APPLICATION_JSON);
//           builder.addPart("employee", body);
//            builder.addPart("employee", body);
//            json.put("FirstName", "badeanda87@hotmail.com");
//            json.put("LastName", "franangthomas@gmail.com");
//            StringEntity se = new StringEntity( json.toString());
//            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            
            post.setEntity(builder.build());
            response = client.execute(post);
            
            /*Checking response */
            if(response!=null){
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
            }
        	}
        	else{
        		String json = "{"+recipient[0]+","+subject+","+body+"}";
        		StringEntity body = new StringEntity(json);
        		body.setContentType("application/json");
        		HttpPost post = new HttpPost("http://192.168.1.4:8080/api/email/");
        		builder.addTextBody("email",json,ContentType.APPLICATION_JSON);
        		
        		post.setEntity(builder.build());
                response = client.execute(post);
                
                if(response!=null){
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                }
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
