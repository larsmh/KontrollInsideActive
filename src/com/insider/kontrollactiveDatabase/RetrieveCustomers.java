package com.insider.kontrollactiveDatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.insider.kontrollactiveModel.Globals;
import com.insider.kontrollactiveModel.User;

import android.os.AsyncTask;
import android.util.Log;

public class RetrieveCustomers extends AsyncTask<String, Integer, Long> {
	
	@Override
	protected Long doInBackground(String... params) {
        String url=params[0]+"customer/?dep="+params[1];
        Log.d("!!!!", url);
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            JSONArray ja = new JSONArray(tokener);
            Globals.custDB.clear();
            for(int i=0; i<ja.length(); i++){
	            JSONObject jo = ja.getJSONObject(i);
	        	String name = jo.getString("Name");
	            String email = jo.getString("Email");
	        	String dept = jo.getString("Department");
	        	Globals.custDB.insert(name, email, dept);
	        	Globals.custList.insert(name, email, dept);
            }
        } catch (Exception e) {
            Log.d("!!!", e.getLocalizedMessage());
        }
		/* NTNU DATABASE
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection(params[0], params[1], params[2]);
	
			Statement stmt = null;
			ResultSet rs = null;
		
	        stmt = con.createStatement();
	        rs = stmt.executeQuery(params[3]);
	        Globals.custDB.clear();
	        while (rs.next()) {
	        	String name = rs.getString("name");
	        	String email = rs.getString("email");
	        	String dept = rs.getString("department");
	        	Globals.custDB.insert(name, email, dept);
	        	Globals.custList.insert(name, email, dept);
	        }
	        con.close();
	    }catch(SQLException e){
	        	e.printStackTrace();
	    }*/
		return null;
	}

}
