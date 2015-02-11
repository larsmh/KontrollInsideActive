package com.insider.kontrollactiveDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.insider.kontrollactiveModel.Globals;
import com.insider.kontrollactiveModel.User;

import android.os.AsyncTask;

public class RetrieveUser extends AsyncTask<String, Integer, Long> {

	@Override
	protected Long doInBackground(String... params) {
        String url=params[0]+"user?pnr="+params[1];
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet=new HttpGet(url);
            httpGet.setHeader("Authorization", "Basic a29udHJvbGxpbnNpZGVhcHBAaW5zaWRlci5ubzp0MnJRZm0yZQ==");
            
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpGet);
 
            // receive response as inputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            
            String json = reader.readLine();
            if(json==null)
            	return null;
            JSONTokener tokener = new JSONTokener(json);
            JSONObject jObject = new JSONObject(tokener);
            Globals.user = new User(jObject.getInt("Id"), jObject.getString("Phonenr"), jObject.getString("Password"), jObject.getString("Department"), jObject.getBoolean("Admin"), jObject.getString("Name"));
            Globals.userFound=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
	
}
