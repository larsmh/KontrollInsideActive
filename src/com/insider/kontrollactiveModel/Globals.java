package com.insider.kontrollactiveModel;
import java.util.ArrayList;

import com.insider.kontrollactive.Mail;
import com.insider.kontrollactiveDatabase.CustomerListDB;

public class Globals {
	public static User user;
	public static CustomerList custList=new CustomerList();
	public static ArrayList<Mail> emaiList = new ArrayList<Mail>();
	public static boolean userFound=false;
	public static CustomerListDB custDB;
}
