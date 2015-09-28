package com.infozimo.adapter;

import java.util.List;

import com.infozimo.android.R;
import com.infozimo.beans.Tag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TagAdapter extends ArrayAdapter<Tag> {

	private int resource;
	
	private LayoutInflater inflater;
	
	public TagAdapter(Context context, int resource, List<Tag> tags) {
		super(context, resource, tags);
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
    public View getView(int position, View v, ViewGroup parent) {
		View view = v;
		if(view == null){
			view = inflater.inflate(resource, null);
		}
		
		TextView textView = (TextView) view.findViewById(R.id.tvTagItem);
		textView.setText(getItem(position).toString());
		
		return view;
	}
	
}
