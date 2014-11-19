package com.insider.kontrollactiveModel;
import java.util.ArrayList;
import java.util.List;

import com.insider.kontrollactive.Email;
import com.insider.kontrollactiveDatabase.CustomerListDB;

public class Globals {
	public static User user;
	//public static CustomerList custList=new CustomerList();
	public static List<Customer> custList = new ArrayList<Customer>();
	public static ArrayList<Email> emaiList = new ArrayList<Email>();
	public static boolean userFound=false;
	public static CustomerListDB custDB;
}
