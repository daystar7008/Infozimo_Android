package com.infozimo.webservice;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

@SuppressWarnings("deprecation")
public class HttpGetRequestTask extends AsyncTask<String, Void, String> {

	private HttpClient client;
	private HttpGet httpGet;
	HttpResponse response;
	HttpEntity entity;
	
	private String url;
	
	public HttpGetRequestTask(String url) {
		this.url = url;
	}
	
	@Override
	protected String doInBackground(String... args) {
		client = new DefaultHttpClient();
		
		httpGet = new HttpGet(url);
		
		String result = null;
		try {
			response = client.execute(httpGet);
			entity = response.getEntity();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			result = EntityUtils.toString(entity);
			Log.d("HttpUtil", result);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
