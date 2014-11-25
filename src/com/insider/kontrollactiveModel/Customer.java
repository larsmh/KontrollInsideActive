package com.insider.kontrollactiveModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable{
	private int id;
	private String name, email, department;
	public Customer(int id, String name, String email, String department){
		this.id=id;
		this.name=name;
		this.email=email;
		this.department=department;
	}

	

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getDepartment() {
		return department;
	}
	public String toString(){
		return name;
	}

	public int getId(){
		return id;
	}
	
	  public Customer(Parcel in){
          String[] data = new String[4];

          in.readStringArray(data);
          this.name = data[0];
          this.email = data[1];
          this.department = data[2];
      }

      public int describeContents(){
          return 0;
      }

      @Override
      public void writeToParcel(Parcel dest, int flags) {
          dest.writeStringArray(new String[] {this.name,
                                              this.email,
                                              this.department});
          int idData = id;
          dest.writeInt(idData);
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
