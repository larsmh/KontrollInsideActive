package com.insider.kontrollactiveModel;


public class User {
	private int id;

	private String phonenr, department, password, name;
	private boolean admin;
	
	public User(int id, String phonenr, String password, String department, boolean admin, String name){
		this.id=id;
		this.phonenr=phonenr;
		this.password=password;
		this.department=department;
		this.admin=admin;
		this.name=name;
	}
	public int getId(){
		return id;
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
