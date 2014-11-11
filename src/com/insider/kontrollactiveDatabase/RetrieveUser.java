package com.insider.kontrollactiveDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.HttpEntity;
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

public class RetrieveUser extends AsyncTask<String, Integer, Long> {

	@Override
	protected Long doInBackground(String... params) {
        String url="http://192.168.1.4:8080/insider/user?pnr="+params[0];
        Log.d("!!!!", url);
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            
            String json = reader.readLine();
            if(json==null)
            	return null;
            JSONTokener tokener = new JSONTokener(json);
            JSONObject jo = new JSONObject(tokener);
            Globals.user = new User(jo.getString("Phonenr"), jo.getString("Password"), jo.getString("Department"), jo.getBoolean("Admin"));
            Globals.userFound=true;
        } catch (Exception e) {
            Log.d("!!!", e.getLocalizedMessage());
        }
        
        /*NTNU DATABASE
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
	        while (rs.next()) {
	        	Globals.userFound=true;
	        	Globals.user = new User(rs.getString("phonenr"), rs.getString("password"), rs.getString("department"), rs.getBoolean("admin"));
	        }
	        con.close();
	    }catch(SQLException e){
	        	e.printStackTrace();
	    }*/
		return null;
	}
	
}
