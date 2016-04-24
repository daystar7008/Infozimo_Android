package com.infozimo.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.infozimo.android.slidingmenu.adapter.NavDrawerListAdapter;
import com.infozimo.android.slidingmenu.model.NavDrawerItem;
import com.infozimo.util.Constants;
import com.infozimo.util.ServiceCaller;
import com.startapp.android.publish.SDKAdPreferences;
import com.startapp.android.publish.SDKAdPreferences.Gender;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;

	private CharSequence mTitle;

	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	private AccessToken accessToken;
	
	private SharedPreferences sharedPref;
	
	private StartAppAd startAppAd = new StartAppAd(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(isNetworkAvailable()){
			FacebookSdk.sdkInitialize(getApplicationContext());
			FacebookSdk.getApplicationSignature(getApplicationContext());
			
			sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			
			Gender gen = null;
			
			String gender = sharedPref.getString(Constants.GENDER, "");
			if("F".equalsIgnoreCase(gender)){
				gen = Gender.FEMALE;
			} else {
				gen = Gender.MALE;
			}
			
			StartAppSDK.init(this, "101777646", "208644780", new SDKAdPreferences().setGender(gen), false);
			
			Profile profile = Profile.getCurrentProfile();
			
			accessToken = AccessToken.getCurrentAccessToken();
			
			if(profile == null){
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				finish();
			} else {
				instantiateUserProfile();
			}
		} else {
			connectivityErrorDialog();
			return;
		}

		setContentView(R.layout.layout_main);
		
		mTitle = mDrawerTitle = getTitle();

		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		/*
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
				.getResourceId(5, -1), true, "50+"));*/

		navMenuIcons.recycle();

		adapter = new NavDrawerListAdapter(getApplicationContext(),	navDrawerItems);
		mDrawerList.setAdapter(adapter);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,	R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			displayView(0);
		}
		
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
	}
	
	private void connectivityErrorDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Internet Connection Not Available");
		dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		
		dialog.show();
	}

	private void displayPoints() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("My Points");
		try {
			ServiceCaller getPointService = new ServiceCaller();
			String pointsJson = getPointService.callGetPointsService(sharedPref.getString(Constants.USER_ID, ""));
			
			JSONObject jsonResponse = new JSONObject(pointsJson);
			
			JSONArray jsonArray = jsonResponse.getJSONArray("points");
			JSONObject jsonObj = jsonArray.getJSONObject(0);
			String points = jsonObj.getString("points");
			
			View view = getLayoutInflater().inflate(R.layout.layout_points, null);
			TextView tvPoints = (TextView) view.findViewById(R.id.tvPoints);
			tvPoints.setText("You Have " + points + " Points");
			view.setTag(points);
			
			Button btnRedeem = (Button) view.findViewById(R.id.btnRedeem);
			btnRedeem.setTag(view);
			btnRedeem.setOnClickListener(new Button.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					View parentView = (View) view.getTag();
					Object points = parentView.getTag();
					if(points != null && Integer.parseInt(points.toString()) >= 10) {
						
						parentView.findViewById(R.id.tvContact).setVisibility(View.VISIBLE);
						
						EditText etContact = (EditText) parentView.findViewById(R.id.etContact);
						etContact.setVisibility(View.VISIBLE);
						
						CheckBox cbxEmail = (CheckBox) parentView.findViewById(R.id.cbxEmail);
						cbxEmail.setVisibility(View.VISIBLE);
						
						String contact = etContact.getText().toString();
						if(contact != null && !contact.equals("")) {
							
							try {
								ServiceCaller redeemService = new ServiceCaller();
								redeemService.callRedeemPointsService(sharedPref.getString(Constants.USER_ID, ""), contact);
							} catch (Exception e) {
								e.printStackTrace();
								Log.e("MainActivity", e.getMessage());
							}
							
							if(cbxEmail.isChecked()) {
								Intent emailIntent = new Intent(Intent.ACTION_SEND);
								emailIntent.setType("message/rfc822");
								emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"rewards@infozimo.com"});
								emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Redeemed " + points + " Points : " + sharedPref.getString(Constants.USER_ID, ""));
								emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, I am Awaiting For PayTM Cash.");
								try {
								    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
								} catch (android.content.ActivityNotFoundException ex) {
								    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
								}
							}
							
						} else {
							Toast.makeText(getApplicationContext(), "Please Fill Contact", Toast.LENGTH_SHORT).show();
						}
						
					} else {
						Toast.makeText(getApplicationContext(), "You Need Minimum 1000 Points to Redeem", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			dialog.setView(view);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("MainActivity", e.getMessage());
		}
		
		dialog.show();
	}
	
	private void instantiateUserProfile() {
		Profile profile = Profile.getCurrentProfile();

		SharedPreferences.Editor spEditor = sharedPref.edit();
		spEditor.putString(Constants.USER_ID, profile.getId());
		spEditor.putString(Constants.USER_NAME, profile.getFirstName());
		spEditor.putString(Constants.ACCESS_TOKEN, accessToken.getToken());
		spEditor.putString(Constants.USER_PIC_URL, profile.getProfilePictureUri(64, 64).toString());
		spEditor.putString(Constants.USER_PIC_URL_128, profile.getProfilePictureUri(128, 128).toString());
		spEditor.apply();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		startAppAd.onResume();
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    startAppAd.onPause();
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("You Want To Exit?");
		dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
				startAppAd.onBackPressed();
				startAppAd.showAd();
				startAppAd.loadAd();
			}
		});
		
		dialog.setNegativeButton("No", null);
		
		dialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		} else {
			if(item.getItemId() == R.id.action_points) {
				displayPoints();
			}
			return false;
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if(mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(mDrawerToggle != null) {
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
	}

	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			displayView(position);
		}
	}

	private void displayView(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new InfoFragment();
			break;
		case 1:
			fragment = new ProfileFragment();
			break;
		case 2:
			fragment = new TagsFragment();
			break;
		case 3:
			fragment = new PostFragment();
			break;
		case 4:
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=rajesh%20ravichandran")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 5:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("How To Get More Cash with Infozimo");
			dialog.setMessage(getString(R.string.howToCash));
			dialog.show();
			break;
		case 6:
			logoutUser();
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void logoutUser(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Are You Sure, You Want to Logout?");
		dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Profile.setCurrentProfile(null);
				AccessToken.setCurrentAccessToken(null);
				Intent intent = new Intent(MainActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		dialog.setNegativeButton("No", null);
		dialog.show();
	}
	
}