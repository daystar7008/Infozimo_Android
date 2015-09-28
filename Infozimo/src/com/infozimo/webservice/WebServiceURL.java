package com.infozimo.webservice;

public enum WebServiceURL {
	
	//SERVICE_URL("http://192.168.1.7/infozimo/services"),
	GET_TAGS("/tags/"),
	GET_TAGS_BY_NAME("/tags/findByTagName/"),
	GET_USER_TAGS("/userTags/"),
	ADD_USER_TAG("/userTags/add"),
	REMOVE_USER_TAG("/userTags/remove"),
	GET_INFO_BY_USERID("/info/findByUserId/"),
	GET_INFO_BY_TAGID("/info/findByTagId/"),
	GET_INFO_BY_USER_TAG("/info/findByUserTag/"),
	ADD_INFO("/info/add"),
	REMOVE_INFO("/info/remove"),
	ADD_LIKE("/like/add"),
	REMOVE_LIKE("/like/remove"),
	GET_USER("/user/"),
	UPDATE_USER("/user/update"),
	POST_TEST("/postTest");

	//private static final String SERVICE_URL = "http://192.168.1.4/infozimo/services";
	private static final String SERVICE_URL = "http://infozimo.com/application/infozimo/services";
	
	private final String value;
	private WebServiceURL(String value){
		this.value = value;
	}
	
	public String toString() {
		return SERVICE_URL + this.value;
	}
	
}
