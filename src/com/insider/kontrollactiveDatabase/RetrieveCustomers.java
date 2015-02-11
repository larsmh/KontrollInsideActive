package com.insider.kontrollactiveDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.Globals;

import android.os.AsyncTask;

public class RetrieveCustomers extends AsyncTask<String, Integer, Long> {
	
	@Override
	protected Long doInBackground(String... params) {
        String url=params[0]+"customer/?dep="+params[1];
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet=new HttpGet(url);
            httpGet.setHeader("Authorization", "Basic a29udHJvbGxpbnNpZGVhcHBAaW5zaWRlci5ubzp0MnJRZm0yZQ==");
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpGet);
 
            // receive response as inputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            JSONArray jArray = new JSONArray(tokener);
            Globals.custDB.clear();
            Globals.custList = new ArrayList<Customer>();
            for(int i=0; i<jArray.length(); i++){
	            JSONObject jObject = jArray.getJSONObject(i);
	            int id = jObject.getInt("Id");
	        	String name = jObject.getString("Name");
	            String email = jObject.getString("Email");
	        	String dept = jObject.getString("Department");
	        	Globals.custDB.insert(id, name, email, dept);
	        	Globals.custList.add(new Customer(id, name, email, dept));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
}
