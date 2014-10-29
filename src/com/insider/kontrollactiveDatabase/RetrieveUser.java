package com.insider.kontrollactiveDatabase;

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

import com.insider.kontrollactiveModel.Globals;
import com.insider.kontrollactiveModel.User;

import android.os.AsyncTask;

public class RetrieveUser extends AsyncTask<String, Integer, Long> {

	@Override
	protected Long doInBackground(String... params) {
		// TODO Auto-generated method stub
		/*
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:53722/insider/user/?pnr="+params[0]);
		
		HttpResponse response;
		try{
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		
		
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
	    }
		return null;
	}

}
