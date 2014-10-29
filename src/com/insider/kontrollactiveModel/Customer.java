package com.insider.kontrollactiveModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable{
	private String name, email, department;
	
	public Customer(String name, String email, String department){
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

	  public Customer(Parcel in){
          String[] data = new String[3];

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
