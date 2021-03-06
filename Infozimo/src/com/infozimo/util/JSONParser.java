package com.infozimo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.infozimo.beans.Info;
import com.infozimo.beans.Tag;

public class JSONParser {
	
	public static List<Tag> parseTags(String json) throws JSONException{
		List<Tag> tags = new ArrayList<Tag>();
		
		JSONObject jsonResponse = new JSONObject(json);
		
		JSONArray jsonArray = jsonResponse.getJSONArray("tags");
		for(int i = 0; i < jsonArray.length(); i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			Tag tag = new Tag();
			
			tag.setTagId(jsonObj.getInt(Tag.TAG_ID));
			tag.setTagName(jsonObj.getString(Tag.TAG_NAME));
			tag.setTagDesc(jsonObj.optString(Tag.TAG_DESC));
			
			tags.add(tag);
		}
		
		return tags;
	}

	public static List<Info> parseInfo(String json) throws JSONException {
		List<Info> infoList = new ArrayList<Info>();
		
		JSONObject jsonResponse = new JSONObject(json);
		
		JSONArray jsonArray = jsonResponse.getJSONArray("info");
		for(int i = 0; i < jsonArray.length(); i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			
			Info info = new Info();
			info.setInfoId(jsonObj.getInt(Info.INFO_ID));
			info.setUserId(jsonObj.getString(Info.USER_ID));
			info.setUserName(jsonObj.getString(Info.USER_NAME));
			info.setTagId(jsonObj.getInt(Info.TAG_ID));
			info.setInfoDetail(jsonObj.getString(Info.INFO_DETAIL));
			info.setInfoPicture(jsonObj.getString(Info.INFO_PICTURE));
			info.setLikeCount(jsonObj.getInt(Info.LIKE_COUNT));
			
			String imageString = jsonObj.optString(Info.PICTURE_BYTES);
			info.setPictureBytes(Base64.decode(imageString, Base64.DEFAULT));
			
			String liked = jsonObj.getString(Info.LIKED);
			if(liked != null && liked.length() > 0){
				info.setLiked(liked.charAt(0));
			}
			
			info.setUserPicUrl(jsonObj.getString(Info.USER_PIC_URL));
			
			infoList.add(info);
		}
		
		return infoList;
	}
	
	public static String jsonOf(Info info) throws JSONException{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(Info.INFO_ID, info.getInfoId());
		jsonObj.put(Info.USER_ID, info.getUserId());
		jsonObj.put(Info.TAG_ID, info.getTagId());
		jsonObj.put(Info.INFO_DETAIL, info.getInfoDetail());
		jsonObj.put(Info.INFO_PICTURE, info.getInfoPicture());
		jsonObj.put(Info.LIKE_COUNT, info.getLikeCount());
		
		return jsonObj.toString();
	}
	
	public static String jsonOf(HashMap<String, Object> values) throws JSONException{
		JSONObject jsonObj = new JSONObject();
		
		for(Map.Entry<String, Object> entry : values.entrySet()){
			jsonObj.put(entry.getKey(), entry.getValue());
		}
		
		return jsonObj.toString();
	}
	
	public static String parseCoverPicUrl(String json) throws JSONException{
		JSONObject jsonObj = new JSONObject(json);
		JSONObject jsonCoverObj = jsonObj.getJSONObject("cover");
		String url = jsonCoverObj.getString("source");
		
		return url;
	}
	
	public static boolean isSucceeded(String json) throws JSONException{
		JSONObject jsonObj = new JSONObject(json);
		int response = Integer.parseInt(jsonObj.getString("response"));
		
		if(response == 1)
			return true;
		else
			return false;
	}
	
}
