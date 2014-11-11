package com.insider.kontrollactiveModel;

public class User {
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
}
