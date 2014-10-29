package com.insider.kontrollactiveModel;

import java.util.ArrayList;
import java.util.List;

public class CustomerList {
	private List<Customer> list;
	
	public CustomerList(){
		list = new ArrayList<Customer>();
	}
	
	public List<Customer> getList(){
		return list;
	}
	public void insert(String name, String email, String dept){
		list.add(new Customer(name, email, dept));
	}
}
