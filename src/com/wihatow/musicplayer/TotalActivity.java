package com.wihatow.musicplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TotalActivity extends Activity {

	ArrayList<String> musicName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_total);
		Intent intent = getIntent();
		musicName = intent.getStringArrayListExtra("musicName");

		ListAdapter adapter = new BaseAdapter() {

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				TextView title = new TextView(TotalActivity.this);
				title.setTextSize(20);
				title.setHeight((int) title.getTextSize() * 3);
				title.setGravity(Gravity.CENTER_VERTICAL);
				title.setText((position + 1) + "、" + musicName.get(position));
				title.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent service = new Intent();
						service.setAction(Information.FROM_LISTACTIVITY);
						service.putExtra("currentPosition", position);
						sendBroadcast(service);

						Intent intent = new Intent(TotalActivity.this,
								PlayActivity.class);
						intent.putExtra("currentPosition", position);
						startActivity(intent);
					}
				});
				return title;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return musicName.size();
			}
		};
		ListView listView = (ListView) findViewById(R.id.music_total_list);
		listView.setAdapter(adapter);
	}
}
