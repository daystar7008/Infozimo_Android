package com.infozimo.android;

import java.util.Arrays;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.infozimo.beans.User;
import com.infozimo.util.Constants;
import com.infozimo.util.ServiceCaller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private CallbackManager callbackManager;
	private LoginButton loginButton;
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor spEditor;
	
	private ProfileTracker profileTracker = null;
	private String gender = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.layout_login);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
		
		callbackManager = CallbackManager.Factory.create();
				
		loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
	        @Override
	        public void onSuccess(LoginResult loginResult) {
        	if(loginResult != null){
        	   
        	   	GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
        	   		@Override
        	   		public void onCompleted(JSONObject object, GraphResponse response) {
        			   gender = object.optString("gender");
        	   		}
					
        	   	});
				
				Bundle parameters = new Bundle();
				parameters.putString("fields", "gender,birthday");
					request.setParameters(parameters);
					request.executeAsync();
	        	   
	        	   profileTracker = new ProfileTracker() {
	        		   @Override
	        		   protected void onCurrentProfileChanged(Profile profile, Profile profile1) {
	        			   profileTracker.stopTracking();
	        			   
	        			   Profile prof =  profile1;
	    	        	   if(prof != null){
	    	        		   User user = new User();
	    	        		   user.setUserId(prof.getId());
	    	        		   user.setUserName(prof.getFirstName());
	    	        		   if(gender != null && gender.length() > 1){
	    	        		   		user.setGender(gender.toUpperCase(Locale.ENGLISH).charAt(0));
	    	        		   }
	    	        		   user.setPicture(prof.getProfilePictureUri(64, 64).toString());
	        					
	    	        		   updateUserProfile(user);
	    	        	   }
	        		   }
	        	   };
	        	   profileTracker.startTracking();
   
	        	   Profile.fetchProfileForCurrentAccessToken();
	        	   Profile profile =  Profile.getCurrentProfile();
	        	   if(profile != null){
	        		   User user = new User();
	        		   user.setUserId(profile.getId());
	        		   user.setUserName(profile.getFirstName());
	        		   if(gender != null && gender.length() > 1){
	        		   		user.setGender(gender.toUpperCase(Locale.ENGLISH).charAt(0));
	        		   }
	        		   user.setPicture(profile.getProfilePictureUri(64, 64).toString());
	        		   
	        		   updateUserProfile(user);
	        	   }
	           }
	        }

	        @Override
	        public void onCancel() {
	            
	        }

	        @Override
	        public void onError(FacebookException exception) {
	        	Toast.makeText(LoginActivity.this, "Couldn't Connect To Facebook", Toast.LENGTH_LONG).show();
	        	Log.e("LoginActivity", exception.getMessage());
	        }
	    });
        
	}
	
	private void updateUserProfile(User user) {
		ServiceCaller serviceCaller = new ServiceCaller();
		try {
			serviceCaller.callUpdateUserService(user);

			spEditor = sharedPref.edit();
			spEditor.putString(Constants.USER_ID, user.getUserId());
			spEditor.putString(Constants.USER_NAME, user.getUserName());
			spEditor.putString(Constants.USER_PIC_URL, user.getPicture());
			spEditor.putString(Constants.GENDER, String.valueOf(user.getGender()));
			spEditor.putString(Constants.ACCESS_TOKEN, AccessToken.getCurrentAccessToken().getToken());
			spEditor.apply();
			
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(callbackManager.onActivityResult(requestCode, resultCode, data))
	    	return;
	}

}