package com.infozimo.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import com.infozimo.beans.Info;
import com.infozimo.beans.Tag;
import com.infozimo.ui.util.DownloadImageTask;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class PostActivity extends Activity {

	private ImageView ivUserPic;
	private TextView tvUser;
	private EditText etInfoDetail;
	private TextView tvSelectedTag;
	private ImageView ivImagePost;
	private GridView gvTags;
	private Button btnChoosePic;
	private Button btnPost;
	private byte[] imageInByte;
	
	private int imgPickResult = 1;
	
	private SharedPreferences sharedPref;
	
	private String userId;
	private Tag selectedTag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_post);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		ivUserPic = (ImageView) findViewById(R.id.ivUserPicPost);
		tvUser  = (TextView) findViewById(R.id.tvUserPost);
		etInfoDetail = (EditText) findViewById(R.id.etInfoDetailPost);
		tvSelectedTag = (TextView) findViewById(R.id.tvSelectedTag);

		ivImagePost = (ImageView) findViewById(R.id.ivImagePost);
		
		userId = sharedPref.getString(Constants.USER_ID, "");
		
		tvUser.setText(sharedPref.getString(Constants.USER_NAME, ""));
		
		String imageUrl = sharedPref.getString(Constants.USER_PIC_URL, "");
		new DownloadImageTask(ivUserPic).execute(imageUrl);
		
		populateTaggedTags();
		
		btnChoosePic = (Button) findViewById(R.id.btnChoosePic);
		btnChoosePic.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Choose Picture"), imgPickResult);
			}
		});
		
		btnPost = (Button) findViewById(R.id.btnPost);
		btnPost.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(selectedTag == null){
					Toast.makeText(PostActivity.this, "Select Tag", Toast.LENGTH_SHORT).show();
					return;
				}
				
				ServiceCaller serviceCaller = new ServiceCaller();
				
				Info info = new Info();
				info.setUserId(userId);
				info.setInfoDetail(etInfoDetail.getText().toString());
				info.setTagId(selectedTag.getTagId());
				info.setInfoPicture(Base64.encodeToString(imageInByte, Base64.DEFAULT));
				
				String response = "";
				try {
					response = serviceCaller.callAddInfoService(info);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				try {
					if(JSONParser.isSucceeded(response)){
						Toast.makeText(PostActivity.this, "Post Successful", Toast.LENGTH_SHORT).show();
						
						Intent intent = new Intent(PostActivity.this, InfoActivity.class);
						startActivity(intent);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
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
		
		if (id == R.id.action_tags) {
			intent = new Intent(this, TagsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		} else if (id == R.id.action_info) {
			intent = new Intent(this, InfoActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    if (requestCode == imgPickResult && resultCode == RESULT_OK && data != null && data.getData() != null) {
	 
	        Uri uri = data.getData();
	        
	        try {
	            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
	            
	            ByteArrayOutputStream stream = new ByteArrayOutputStream();   
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
	            imageInByte = stream.toByteArray(); 
	            
	            //Toast.makeText(this, String.valueOf(lengthbmp), Toast.LENGTH_LONG).show();
	            ivImagePost.setImageBitmap(bitmap);
	            ivImagePost.setVisibility(ImageView.VISIBLE);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
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
			gvTags = (GridView) findViewById(R.id.gvTagsPost);
			ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(this, R.layout.tag_item, tags);
			gvTags.setAdapter(adapter);
			gvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> list, View arg1, int position, long arg3) {
					selectedTag = (Tag) list.getItemAtPosition(position);
					tvSelectedTag.setText(selectedTag.getTagName());
					tvSelectedTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close, 0);
					tvSelectedTag.setVisibility(TextView.VISIBLE);
					
					tvSelectedTag.setOnClickListener(new TextView.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							tvSelectedTag.setVisibility(TextView.INVISIBLE);
							selectedTag = null;
						}
					});
				}
			});
			
			adapter.notifyDataSetChanged();
		}
	}
	
}
