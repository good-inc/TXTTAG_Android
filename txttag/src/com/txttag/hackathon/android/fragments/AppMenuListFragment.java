package com.txttag.hackathon.android.fragments;

import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.activities.AboutActivity;
import com.txttag.hackathon.android.activities.FaqActivity;
import com.txttag.hackathon.android.activities.FeedbackActivity;
import com.txttag.hackathon.android.activities.RegisterTagActivity;
import com.txttag.hackathon.android.activities.SendMessageActivity;
import com.txttag.hackathon.android.activities.SettingsActivity;
import com.txttag.hackathon.android.activities.TermsActivity;
import com.txttag.hackathon.android.activities.ViewMyMessagesActivity;
import com.txttag.hackathon.android.app.TxtTagStats;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppMenuListFragment extends ListFragment {

	private static final String TAG = "AppMenuListFragment";
	
	private SampleAdapter adapter;
	private TextView stats;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.list, null);
		
		stats = (TextView) layout.findViewById(R.id.stats);
		stats.setText("Stats: " + TxtTagStats.getInstance().getNumTags() + " Users, " + TxtTagStats.getInstance().getNumMessages() + " Messages");
		
		return layout;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new SampleAdapter(getActivity());
		
		// Add menu items
		adapter.add(new SampleItem("Txt A Tag", R.drawable.ic_menu_send, new Intent(this.getActivity(), SendMessageActivity.class)));
		adapter.add(new SampleItem("Register A Tag", R.drawable.ic_menu_add, new Intent(this.getActivity(), RegisterTagActivity.class)));
		adapter.add(new SampleItem("View My Messages", R.drawable.ic_menu_friendslist, new Intent(this.getActivity(), ViewMyMessagesActivity.class)));
		adapter.add(new SampleItem("Settings", R.drawable.ic_menu_gear, new Intent(this.getActivity(), SettingsActivity.class)));
		adapter.add(new SampleItem("About", R.drawable.ic_menu_info_details, new Intent(this.getActivity(), AboutActivity.class)));
		adapter.add(new SampleItem("FAQ", R.drawable.ic_menu_info_details, new Intent(this.getActivity(), FaqActivity.class)));
		adapter.add(new SampleItem("Terms", R.drawable.ic_menu_info_details, new Intent(this.getActivity(), TermsActivity.class)));
		adapter.add(new SampleItem("Feedback", R.drawable.ic_menu_info_details, new Intent(this.getActivity(), FeedbackActivity.class)));
		setListAdapter(adapter);
		
		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(adapter.getItem(position).clickAction);
			}
		});
		
	}

	private class SampleItem {
		public String tag;
		public int iconRes;
		public Intent clickAction;
		public SampleItem(String tag, int iconRes, Intent clickAction) {
			this.tag = tag; 
			this.iconRes = iconRes;
			this.clickAction = clickAction;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			Typeface grunge = Typeface.createFromAsset(getContext().getAssets(), "fonts/viper.ttf");
			title.setTypeface(grunge);
			title.setTextSize(24);
			title.setTextColor(0xFFFFFFFF);
			convertView.setBackgroundColor(0xFF0088FF);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}
}
