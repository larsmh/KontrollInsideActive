package com.insider.kontrollactiveDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class RegisterJob extends AsyncTask<String, Integer, Long>{

	@Override
	protected Long doInBackground(String... params) {
		// TODO Auto-generated method stub
		String url=params[0]+"job/?cust="+params[1]+"&user="+params[2]+"&date="+params[3];
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", "Basic a29udHJvbGxpbnNpZGVhcHBAaW5zaWRlci5ubzp0MnJRZm0yZQ==");
            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

}
