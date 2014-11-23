package com.insider.kontrollactiveModel;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
	private String phonenr, department, password, name;
	private boolean admin;
	
	public User(String phonenr, String password, String department, boolean admin){
		this.phonenr=phonenr;
		this.password=password;
		this.department=department;
		this.name = name;
		this.admin=admin;
	}
	public User(String phonenr, boolean admin){
		this.phonenr=phonenr;
		this.department="n/a";
		this.admin=admin;
	}
	public String getPhonenr() {
		return phonenr;
	}
	public String getPassword(){
		return password;
	}

	public String getDepartment() {
		return department;
	}
	
	public String getName(){
		return name;
	}
	
	public void setDepartment(String department){
		this.department=department;
	}
	
	public boolean getAdmin(){
		return admin;
	}
	public User(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.name = data[0];
       
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in); 
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
