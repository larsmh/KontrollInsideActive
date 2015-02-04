package com.insider.kontrollactiveDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class RegisterJob extends AsyncTask<String, Integer, Long>{

	@Override
	protected Long doInBackground(String... params) {
		// TODO Auto-generated method stub
		String url=params[0]+"job/?cust="+params[1]+"&user="+params[2]+"&date="+params[3];
        Log.d("!!!!", url);
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", "Basic a29udHJvbGxpbnNpZGVhcHBAaW5zaWRlci5ubzp0MnJRZm0yZQ==");
            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
        } catch (Exception e) {
            Log.d("!!!", e.getLocalizedMessage());
        }
		return null;
	}

}
