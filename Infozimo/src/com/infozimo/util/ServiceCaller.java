package com.infozimo.util;

import java.util.HashMap;

import org.json.JSONException;

import android.util.Log;

import com.infozimo.beans.Info;
import com.infozimo.beans.Tag;
import com.infozimo.beans.User;
import com.infozimo.webservice.HttpGetRequestTask;
import com.infozimo.webservice.HttpPostRequestTask;
import com.infozimo.webservice.WebServiceURL;

public class ServiceCaller {
	
	private HttpGetRequestTask getRequest;
	private HttpPostRequestTask postRequest;
	
	public String callTagService(String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_TAGS.toString() + userId);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
		
	}
	
	public String callTagByNameService(String tagName, String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_TAGS_BY_NAME.toString() + tagName + "/" + userId);
		Log.d("URL", WebServiceURL.GET_TAGS_BY_NAME.toString() + tagName + "/" + userId);
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUserTagService(String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_USER_TAGS.toString() + userId);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUserIdInfoService(String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_INFO_BY_USERID.toString() + userId);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callTagIdInfoService(String tagId, String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_INFO_BY_TAGID.toString() + tagId + "/" + userId);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUserTagInfoService(String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_INFO_BY_USER_TAG.toString() + userId);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callAddUserTagService(String userId, int tagId){
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Tag.TAG_ID, tagId);
		values.put("user_id", userId);
		
		try {
			postRequest = new HttpPostRequestTask(WebServiceURL.ADD_USER_TAG.toString(), JSONParser.jsonOf(values));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callRemoveUserTagService(String userId, int tagId){
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Tag.TAG_ID, tagId);
		values.put("user_id", userId);
		
		try {
			postRequest = new HttpPostRequestTask(WebServiceURL.REMOVE_USER_TAG.toString(), JSONParser.jsonOf(values));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callAddInfoService(Info info) throws JSONException{
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Info.USER_ID, info.getUserId());
		values.put(Info.TAG_ID, info.getTagId());
		values.put(Info.INFO_DETAIL, info.getInfoDetail());
		//values.put(Info.PICTURE_BYTES, info.getPictureBytes());
		values.put(Info.INFO_PICTURE, info.getInfoPicture());
		
		postRequest = new HttpPostRequestTask(WebServiceURL.ADD_INFO.toString(), JSONParser.jsonOf(info));
		//getRequest = new HttpGetRequestTask("http://192.168.1.6/infozimo/services/test/createFile");
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callRemoveInfoService(String infoId){
		postRequest = new HttpPostRequestTask(WebServiceURL.REMOVE_INFO.toString() + infoId);
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callAddLikeService(int infoId, String userId) throws JSONException{
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Info.INFO_ID, infoId);
		values.put("user_id", userId);
		
		postRequest = new HttpPostRequestTask(WebServiceURL.ADD_LIKE.toString(), JSONParser.jsonOf(values));
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callRemoveLikeService(int infoId, String userId) throws JSONException{
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Info.INFO_ID, infoId);
		values.put("user_id", userId);
		
		postRequest = new HttpPostRequestTask(WebServiceURL.REMOVE_LIKE.toString(), JSONParser.jsonOf(values));
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callGetUserService(String userId) throws JSONException{
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_USER.toString() + "userId");
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUpdateUserService(User user) throws JSONException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(User.USER_ID, user.getUserId());
		values.put(User.USER_NAME, user.getUserName());
		values.put(User.GENDER, user.getGender());
		values.put(User.DOB, user.getDob());
		values.put(User.PICTURE, user.getPicture());
		
		postRequest = new HttpPostRequestTask(WebServiceURL.UPDATE_USER.toString(), JSONParser.jsonOf(values));
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}

}
