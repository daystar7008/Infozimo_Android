package com.infozimo.beans;

public class User {

	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String GENDER = "gender";
	public static final String DOB = "dob";
	public static final String PICTURE = "picture";
	
	private String userId;
	private String userName;
	private char gender;
	private String dob;
	private String picture;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
}
