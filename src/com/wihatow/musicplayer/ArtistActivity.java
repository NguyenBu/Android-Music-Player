package com.wihatow.musicplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArtistActivity extends Activity {

	ArrayList<String> musicName;
	ArrayList<String> musicArtist;
	ArrayList<ArrayList<String>> artistList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist);
		Intent intent = getIntent();
		musicName = intent.getStringArrayListExtra("musicName");
		musicArtist = intent.getStringArrayListExtra("musicArtist");
		artistList = getArtistList();

		ExpandableListAdapter adapter = new BaseExpandableListAdapter() {

			@Override
			public boolean isChildSelectable(int groupPosition,
					int childPosition) {
				return true;
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				LinearLayout layout = new LinearLayout(ArtistActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);
				TextView textView = new TextView(ArtistActivity.this);
				TextView remarks = new TextView(ArtistActivity.this);
				textView.setTextSize(20);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				remarks.setTextSize(18);
				remarks.setHeight((int) remarks.getTextSize() * 3);
				remarks.setGravity(Gravity.CENTER_VERTICAL);
				String string = artistList.get(groupPosition).get(0);
				String artist = string.substring(string.indexOf("`") + 1);
				textView.setText(artist);
				remarks.setText("共" + artistList.get(groupPosition).size()
						+ "首歌");
				layout.addView(textView);
				layout.addView(remarks);
				return layout;
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public int getGroupCount() {
				return artistList.size();
			}

			@Override
			public Object getGroup(int groupPosition) {
				return null;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return artistList.get(groupPosition).size();
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				TextView textView = new TextView(ArtistActivity.this);
				String string = artistList.get(groupPosition)
						.get(childPosition);
				String name = (childPosition + 1)
						+ "、"
						+ string.substring(string.indexOf("|") + 1,
								string.indexOf("`"));
				final int currentPosition = Integer.parseInt(string.substring(
						0, string.indexOf("|")));
				textView.setTextSize(18);
				textView.setText(name);
				textView.setHeight((int) textView.getTextSize() * 3);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent service = new Intent();
						service.setAction(Information.FROM_LISTACTIVITY);
						service.putExtra("currentPosition", currentPosition);
						sendBroadcast(service);

						Intent intent = new Intent(ArtistActivity.this,
								PlayActivity.class);
						intent.putExtra("currentPosition", currentPosition);
						startActivity(intent);
					}
				});
				return textView;
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}

			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return null;
			}
		};
		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.music_artist_list);
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(adapter);
	}

	private ArrayList<ArrayList<String>> getArtistList() {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		ArrayList<String> temslist = new ArrayList<String>();
		for (String temp : musicArtist) {
			if (!temslist.contains(temp)) {
				temslist.add(temp);
			}
		}

		for (int i = 0; i < temslist.size(); i++) {
			ArrayList<String> temlist = new ArrayList<String>();
			for (int j = 0; j < musicArtist.size(); j++) {
				if (temslist.get(i).equals(musicArtist.get(j))) {
					temlist.add(j + "|" + musicName.get(j) + "`"
							+ musicArtist.get(j));
				}
			}
			list.add(temlist);
		}
		return list;
	}
}
