package com.infozimo.android;

import java.util.Arrays;

import org.json.JSONException;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private CallbackManager callbackManager;
	private LoginButton loginButton;
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor spEditor;
	private AccessToken accessToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.layout_login);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		accessToken = AccessToken.getCurrentAccessToken();
		
		loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setReadPermissions(Arrays.asList("public_profile"));
		
		//if(isFirstLaunch && userId != null && !"".equals(userId)){
		if(accessToken != null){
			loginButton.setVisibility(View.GONE);
			instantiateUserProfile();
		} else {
			callbackManager = CallbackManager.Factory.create();
	        
			loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
		        @Override
		        public void onSuccess(LoginResult loginResult) {
		           if(loginResult != null){
		        	   accessToken = AccessToken.getCurrentAccessToken();
		        	   instantiateUserProfile();
		           }
		        }

		        @Override
		        public void onCancel() {
		            // App code
		        }

		        @Override
		        public void onError(FacebookException exception) {
		        	Toast.makeText(LoginActivity.this, "Couldn't Connect To Facebook", Toast.LENGTH_LONG).show();
		        }
		    });
		}
        
	}
	
	private void instantiateUserProfile() {
		Profile profile = Profile.getCurrentProfile();
		
		User user = new User();
		user.setUserId(profile.getId());
		user.setUserName(profile.getFirstName());
		user.setPicture(profile.getProfilePictureUri(64, 64).toString());

		ServiceCaller serviceCaller = new ServiceCaller();
		try {
			serviceCaller.callUpdateUserService(user);

			spEditor = sharedPref.edit();
			spEditor.putString(Constants.USER_ID, user.getUserId());
			spEditor.putString(Constants.USER_NAME, user.getUserName());
			spEditor.putString(Constants.USER_PIC_URL, user.getPicture());
			spEditor.apply();

			if(accessToken != null) {
				Intent infoIntent = new Intent(LoginActivity.this, InfoActivity.class);
				infoIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(infoIntent);
				finish();
			} else {
				Intent tagIntent = new Intent(LoginActivity.this, TagsActivity.class);
				tagIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(tagIntent);
				finish();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Intent intent = null;
		
		if (id == R.id.action_post) {
			intent = new Intent(this, PostActivity.class);
			intent.putExtra(Constants.USER_PIC_URL, sharedPref.getString(Constants.USER_PIC_URL, ""));
			startActivity(intent);
		} else if (id == R.id.action_tags) {
			intent = new Intent(this, TagsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (id == R.id.action_info) {
			intent = new Intent(this, InfoActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
