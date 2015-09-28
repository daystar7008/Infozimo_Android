package com.infozimo.android;

import java.util.List;

import org.json.JSONException;

import com.infozimo.beans.Tag;
import com.infozimo.listeners.TagClickListener;
import com.infozimo.listeners.TagClickListener.TransType;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;

import android.text.Editable;
import android.text.TextWatcher;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

public class TagsActivity extends Activity {

	private SharedPreferences sharedPref;
	
	private GridView gridViewTags;
	private Button btnTags, btnMyTags;
	private EditText etTagSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_tags);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		TagToggleListener toggleListener = new TagToggleListener();
		
		btnTags = (Button) findViewById(R.id.btnTags);
		btnTags.setOnClickListener(toggleListener);
		
		btnMyTags = (Button) findViewById(R.id.btnMyTags);
		btnMyTags.setOnClickListener(toggleListener);
		
		TagSearchListener searchListener = new TagSearchListener();
		
		etTagSearch = (EditText) findViewById(R.id.etTagSearch);
		etTagSearch.addTextChangedListener(searchListener);
		
		populateUnTaggedTags();
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
		if (id == R.id.action_info) {
			intent = new Intent(this, InfoActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, InfoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}

	private void populateUnTaggedTags(){
		ServiceCaller serviceCaller = new ServiceCaller();
		String response = serviceCaller.callTagService(sharedPref.getString(Constants.USER_ID, ""));
		
		List<Tag> tags =  null;
		try {
			tags = JSONParser.parseTags(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(tags != null){
			gridViewTags = (GridView) findViewById(R.id.gridViewTags);
			ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(this, R.layout.tag_item, tags);
			gridViewTags.setAdapter(adapter);
			etTagSearch.setVisibility(View.VISIBLE);
			gridViewTags.setOnItemClickListener(new TagClickListener(TransType.ADD));
			adapter.notifyDataSetChanged();
		}
	}
	
	private void populateTaggedTags(){
		ServiceCaller serviceCaller = new ServiceCaller();
		String response = serviceCaller.callUserTagService(sharedPref.getString(Constants.USER_ID, ""));
		
		List<Tag> tags =  null;
		try {
			tags = JSONParser.parseTags(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(tags != null){
			gridViewTags = (GridView) findViewById(R.id.gridViewTags);
			ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(this, R.layout.tag_item, tags);
			gridViewTags.setAdapter(adapter);
			etTagSearch.setVisibility(View.GONE);
			gridViewTags.setOnItemClickListener(new TagClickListener(TransType.REMOVE));
			adapter.notifyDataSetChanged();
		}
	}
	
	private void findTagsByName(){
		String searchText = etTagSearch.getText().toString();
		
		ServiceCaller serviceCaller = new ServiceCaller();
		String response = serviceCaller.callTagByNameService(searchText, sharedPref.getString(Constants.USER_ID, ""));
		
		List<Tag> tags =  null;
		try {
			tags = JSONParser.parseTags(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(tags != null){
			gridViewTags = (GridView) findViewById(R.id.gridViewTags);
			ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(this, R.layout.tag_item, tags);
			gridViewTags.setAdapter(adapter);
			etTagSearch.setVisibility(View.VISIBLE);
			gridViewTags.setOnItemClickListener(new TagClickListener(TransType.ADD));
			adapter.notifyDataSetChanged();
		}
	}
	
	public class TagToggleListener implements Button.OnClickListener {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			if(id == R.id.btnTags){
				populateUnTaggedTags();
			}
			else if(id == R.id.btnMyTags){
				populateTaggedTags();
			}
		}
		
	}
	
	public class TagSearchListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			
		}

		@Override
		public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
			if(text.length() > 2){
				findTagsByName();
			} else {
				populateUnTaggedTags();
			}
		}
		
	}
	
}
