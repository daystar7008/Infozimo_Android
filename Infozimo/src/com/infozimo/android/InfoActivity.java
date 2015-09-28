package com.infozimo.android;

import java.util.List;

import com.infozimo.adapter.InfoAdapter;
import com.infozimo.beans.Info;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class InfoActivity extends Activity {

	private SharedPreferences sharedPref;
	
	private ListView lvInfoList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_info);
		
		getActionBar().setTitle(R.string.app_name);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		populateInfo();
		
	}

	private void populateInfo() {
		ServiceCaller serviceCaller = new ServiceCaller();
		String response = serviceCaller.callUserTagInfoService(sharedPref.getString(Constants.USER_ID, ""));
		List<Info> infoList = null;
		try {
			infoList = JSONParser.parseInfo(response);
			
			if(infoList != null && infoList.size() < 1){
				openFirstLaunchDialog();
				return;
			}
		} catch (Exception e) {
			Log.e("InfoActivity.Java", e.getMessage());
		}
		
		if(infoList != null){
			lvInfoList = (ListView) findViewById(R.id.lvInfo);
			InfoAdapter infoAdapter = new InfoAdapter(this, R.layout.view_info, infoList);
			lvInfoList.setAdapter(infoAdapter);
		}
	}
	
	private void openFirstLaunchDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Have You Selected Any Tags?");
		dialog.setMessage("We'll Help You Out To Setup");
		
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent tagIntent = new Intent(InfoActivity.this, TagsActivity.class);
				tagIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(tagIntent);
				finish();
			}
		});
		
		dialog.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		int id = item.getItemId();
		if (id == R.id.action_tags) {
			intent = new Intent(this, TagsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		} else if (id == R.id.action_post) {
			intent = new Intent(this, PostActivity.class);
			intent.putExtra(Constants.USER_PIC_URL, sharedPref.getString(Constants.USER_PIC_URL, ""));
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
