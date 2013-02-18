package com.txttag.hackathon.android.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.models.TagMessage;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TagMessageAdapter extends ArrayAdapter<TagMessage> 
{
	public TagMessageAdapter(Context context, ArrayList<TagMessage> items)
	{
		super(context, R.layout.message_list_row, items);
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.message_list_row, null);
		}
		
		TagMessage item = this.getItem(position);
		if (item != null)
		{
			TextView messageBlurb = (TextView)view.findViewById(R.id.message_blurb);
			Typeface messageType = Typeface.createFromAsset(getContext().getAssets(), "fonts/big_noodle_titling.ttf");
			messageBlurb.setTypeface(messageType);
			messageBlurb.setTextSize(28);
			messageBlurb.setText(item.message);
			
			TextView messageDate = (TextView)view.findViewById(R.id.message_date);
			Typeface dateType = Typeface.createFromAsset(getContext().getAssets(), "fonts/opificio.ttf");
			messageDate.setTypeface(dateType);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");
			try {
				Date date = dateFormat.parse(item.date);
				dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mma");
				messageDate.setText(dateFormat.format(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				messageDate.setText(item.date);
			}
			
		}
		
		return view;
	}
}
