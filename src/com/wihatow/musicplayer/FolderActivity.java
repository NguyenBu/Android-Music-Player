package com.wihatow.musicplayer;

import java.io.File;
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

public class FolderActivity extends Activity {

	ArrayList<String> musicList;
	ArrayList<String> musicName;
	ArrayList<ArrayList<String>> folderList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder);
		Intent intent = getIntent();
		musicList = intent.getStringArrayListExtra("musicList");
		musicName = intent.getStringArrayListExtra("musicName");
		folderList = getFolderList();

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
				LinearLayout layout = new LinearLayout(FolderActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);
				TextView textView = new TextView(FolderActivity.this);
				TextView remarks = new TextView(FolderActivity.this);
				textView.setTextSize(20);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				remarks.setTextSize(18);
				remarks.setHeight((int) remarks.getTextSize() * 3);
				remarks.setGravity(Gravity.CENTER_VERTICAL);
				String string = folderList.get(groupPosition).get(0);
				String folder = string.substring(string.indexOf("`") + 1);
				textView.setText(folder);
				remarks.setText("共" + folderList.get(groupPosition).size()
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
				return folderList.size();
			}

			@Override
			public Object getGroup(int groupPosition) {
				return null;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return folderList.get(groupPosition).size();
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				TextView textView = new TextView(FolderActivity.this);
				String string = folderList.get(groupPosition)
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

						Intent intent = new Intent(FolderActivity.this,
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
		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.music_folder_list);
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(adapter);
	}

	private ArrayList<ArrayList<String>> getFolderList() {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		ArrayList<String> temlist = new ArrayList<String>();
		ArrayList<String> fileList = new ArrayList<String>();
		for (int i = 0; i < musicList.size(); i++) {
			String path = new File(musicList.get(i)).getParent();
			temlist.add(path);
		}
		for (String temp : temlist) {
			if (!fileList.contains(temp)) {
				fileList.add(temp);
			}
		}

		for (int i = 0; i < fileList.size(); i++) {
			ArrayList<String> temslist = new ArrayList<String>();
			for (int j = 0; j < temlist.size(); j++) {
				if (fileList.get(i).equals(temlist.get(j))) {
					temslist.add(j + "|" + musicName.get(j) + "`"
							+ temlist.get(j));
				}
			}
			list.add(temslist);
		}
		return list;
	}
}
