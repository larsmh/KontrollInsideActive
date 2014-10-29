package com.insider.kontrollactiveDatabase;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.insider.kontrollactiveModel.Globals;

import android.os.AsyncTask;
import android.util.Log;

public class DBThread extends AsyncTask<String, Integer, Long> {

	@Override
	protected Long doInBackground(String... params) {
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("!!!", "class!!!!");
		}
		try {
			Log.d("!!!", "Trying");
			con = DriverManager.getConnection(params[0], params[1], params[2]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("!!!", "Fail");
		}
		Statement stmt = null;
		ResultSet rs = null;
		try{
	        stmt = con.createStatement();
	        rs = stmt.executeQuery(params[3]);
	        while (rs.next()) {
	        	String name = rs.getString("name");
	        	String email = rs.getString("email");
	        	String dept = rs.getString("department");
	        	Globals.custList.insert(name, email, dept);
	        	//Log.d("!!!", name+" "+email+" "+dept);
	        }
	    }catch(Exception e){
	        	e.printStackTrace();
	        	Log.d("!!!!", "sql");
	    }
		return null;
	}

}
