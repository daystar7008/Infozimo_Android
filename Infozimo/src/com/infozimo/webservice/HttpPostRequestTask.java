package com.infozimo.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

@SuppressWarnings("deprecation")
public class HttpPostRequestTask extends AsyncTask<String, Void, String> {

	private HttpClient client;
	private HttpPost post;
	
	private String url;
	private String jsonParam;
	
	public HttpPostRequestTask(String url) {
		this.url = url;
	}
	
	public HttpPostRequestTask(String url, String jsonParam) {
		this.url = url;
		this.jsonParam = jsonParam;
	}
	
	@Override
	protected String doInBackground(String... args) {
		client = new DefaultHttpClient();
		post = new HttpPost(url);
		
		/*HttpParams params = null;
		
		if(jsonParam != null){
			params = new BasicHttpParams();
			params.setParameter("json", jsonParam);
			
			post.setParams(params);
		}*/
		
		if(jsonParam != null){
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("json", jsonParam));
			UrlEncodedFormEntity formEntity = null;
			try {
				formEntity = new UrlEncodedFormEntity(params);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			if(formEntity != null){
				post.setEntity(formEntity);
			}
		}
		
		HttpResponse response = null;
		HttpEntity entity = null;
		
		String result = null;
		try {
			response = client.execute(post);
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
