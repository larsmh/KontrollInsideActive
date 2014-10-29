package com.insider.kontrollactiveDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import android.os.AsyncTask;

public class RegisterJob extends AsyncTask<String, Integer, Long>{

	@Override
	protected Long doInBackground(String... params) {
		// TODO Auto-generated method stub
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
	        stmt = con.createStatement();
	        stmt.executeUpdate(params[3]);
	        
	        con.close();
	    }catch(SQLException e){
	        	e.printStackTrace();
	    }
		return null;
	}

}
