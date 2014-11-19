package com.insider.kontrollactiveDatabase;

import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.Globals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomerListDB extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "Customers.db"; 
	
	public CustomerListDB(Context context) {
		super(context, DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE customers "
				+ "(id integer primary key, name text, email text, department text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS customers");
		onCreate(db);
	}
	public boolean insert(int id, String name, String email, String department){
		SQLiteDatabase db = this.getWritableDatabase();
	    ContentValues contentValues = new ContentValues();
	    
	    contentValues.put("id", id);
	    contentValues.put("name", name);
		contentValues.put("email", email);	
		contentValues.put("department", department);
		
		db.insert("customers", null, contentValues);
		return true;
	}
	
	public void getData(){
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor rs =  db.rawQuery( "select * from customers", null );
	      while(rs.moveToNext()){
	    	  int id = rs.getInt(rs.getColumnIndex("name"));
	    	  String name = rs.getString(rs.getColumnIndex("name"));
	    	  String email = rs.getString(rs.getColumnIndex("email"));
	    	  String dept = rs.getString(rs.getColumnIndex("department"));
	          Globals.custList.add(new Customer(id, name, email, dept));
	      }
	}
	public void clear(){
		SQLiteDatabase db = this.getWritableDatabase();
		onUpgrade(db, 1, 1);
	}

}
