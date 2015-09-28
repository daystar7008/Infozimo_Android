package com.infozimo.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.infozimo.android.R;
import com.infozimo.beans.Info;
import com.infozimo.ui.util.DownloadImageTask;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InfoAdapter extends ArrayAdapter<Info> {

	private SharedPreferences sharedPref;
	
	private Context mContext = null;
    private int id;
    
    private LikeClickListener likeListener;
    
    private LayoutInflater inflater;
    
    private Drawable drawableUnlike;
    private Drawable drawableLike;
	
	public InfoAdapter(Context context, int resource, List<Info> infoList) {
		super(context, resource, infoList);
		mContext = context;
        id = resource;
        
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        
        drawableUnlike = mContext.getResources().getDrawable(R.drawable.ic_liked);
        drawableLike = mContext.getResources().getDrawable(R.drawable.ic_like);
	}
	
	@Override
    public View getView(int position, View v, ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();
		
        View mView = v;
        if(mView == null){
            mView = inflater.inflate(id, null);
            viewHolder.tvUser = (TextView) mView.findViewById(R.id.tvUser);
            viewHolder.ivUserPic = (ImageView) mView.findViewById(R.id.ivUserPic);
            viewHolder.tvInfoDetail = (TextView) mView.findViewById(R.id.tvInfoDetail);
            viewHolder.layoutImageInfo = (LinearLayout) mView.findViewById(R.id.layoutImageInfo);
            viewHolder.ivInfoImage = (ImageView) mView.findViewById(R.id.ivInfoImage);
            viewHolder.ibShareInfo = (ImageButton) mView.findViewById(R.id.ibShareInfo);
            viewHolder.ibLikeInfo = (ImageButton) mView.findViewById(R.id.ibLikeInfo);
            mView.setTag(viewHolder);
        } else {
        	viewHolder = (ViewHolder) mView.getTag();
        }
        
        Info info = getItem(position);
        
        viewHolder.tvUser.setText(info.getUserName());
        
        new DownloadImageTask(viewHolder.ivUserPic).execute(info.getUserPicUrl());
        
        viewHolder.tvInfoDetail.setText(info.getInfoDetail());
        
        byte[] imageBytes = info.getPictureBytes();
        if(imageBytes != null){
        	Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        	viewHolder.ivInfoImage.setImageBitmap(bmp);
        	
        	viewHolder.layoutImageInfo.setVisibility(LinearLayout.VISIBLE);
        } else {
        	/*viewHolder.tvReplacer = new TextView(mContext);
        	viewHolder.layoutImageInfo.addView(viewHolder.tvReplacer);
        	viewHolder.layoutImageInfo.setVisibility(LinearLayout.VISIBLE);*/
        }
        
        viewHolder.values.put(Constants.INFO, info);
        viewHolder.values.put(Constants.USER_ID, sharedPref.getString(Constants.USER_ID, ""));
    	viewHolder.values.put("position", position);
    	
        if(info.getLiked() != '\u0000' && info.getLiked() == 'Y'){
        	viewHolder.ibLikeInfo.setImageDrawable(drawableUnlike);
        	viewHolder.values.put(Constants.LIKE_STATUS, Action.UNLIKE);
        } else {
        	viewHolder.ibLikeInfo.setImageDrawable(drawableLike);
        	viewHolder.values.put(Constants.LIKE_STATUS, Action.LIKE);
        }
        viewHolder.ibLikeInfo.setTag(viewHolder.values);
        likeListener = new LikeClickListener();
        viewHolder.ibLikeInfo.setOnClickListener(likeListener);
        
        viewHolder.ibShareInfo.setTag(viewHolder.values);
        viewHolder.ibShareInfo.setOnClickListener(new ShareClickListener());
        
        return mView;
	}
	
	private static class ViewHolder {
		TextView tvUser;
		ImageView ivUserPic;
		TextView tvInfoDetail;
		LinearLayout layoutImageInfo;
		ImageView ivInfoImage;
		TextView tvReplacer;
		ImageButton ibShareInfo;
		ImageButton ibLikeInfo;
		
		HashMap<String, Object> values = new HashMap<String, Object>();
	}
	
	private class LikeClickListener implements OnClickListener {

		private int infoId;
		private String userId;
		private Action action;
		private Info info;
		
		private HashMap<String, Object> values;
		
		@Override
		public void onClick(View v) {
			ServiceCaller serviceCaller = new ServiceCaller();
			values = (HashMap<String, Object>) v.getTag();
			action = (Action) values.get(Constants.LIKE_STATUS);
			userId = (String) values.get(Constants.USER_ID);
			info = (Info) values.get(Constants.INFO);
			infoId = info.getInfoId();
			
			String response = null;
			try {
				if(Action.LIKE.equals(action)){
					response = serviceCaller.callAddLikeService(infoId, userId);
				} else if(Action.UNLIKE.equals(action)){
					response = serviceCaller.callRemoveLikeService(infoId, userId);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			try {
				if(response != null && JSONParser.isSucceeded(response)){
					if(Action.LIKE.equals(action)){
			        	info.setLiked('Y');
			        	notifyDataSetChanged();
					} else if(Action.UNLIKE.equals(action)){
						info.setLiked('N');
			        	notifyDataSetChanged();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class ShareClickListener implements OnClickListener {

		private int infoId;
		private String userId;
		private Info info;
		
		private HashMap<String, Object> values;
		
		@Override
		public void onClick(View v) {
			values = (HashMap<String, Object>) v.getTag();
			userId = (String) values.get(Constants.USER_ID);
			info = (Info) values.get(Constants.INFO);
			infoId = info.getInfoId();
			
			Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
			intent.setType("*/*");
			intent.putExtra(Intent.EXTRA_TEXT, info.getInfoDetail());
			//http://stackoverflow.com/questions/7661875/how-to-use-share-image-using-sharing-intent-to-share-images-in-android
			//intent.putExtra(Intent.EXTRA_STREAM, info.getPictureBytes());
			mContext.startActivity(intent);
		}
		
	}

	public enum Action {
		LIKE, UNLIKE;
	}
	
	private void createTempPicture(byte[] imageBytes){
		/*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temp" +".jpg");
		try {
		    f.createNewFile();
		    FileOutputStream fo = new FileOutputStream(f);
		    fo.write(imageBytes.toByteArray());
		} catch (IOException e) {                       
		        e.printStackTrace();
		}*/
	}
	
}
