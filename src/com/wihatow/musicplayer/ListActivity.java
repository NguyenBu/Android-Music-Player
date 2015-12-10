package com.wihatow.musicplayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends Activity {
	TextView textView;
	ArrayList<String> musicList = new ArrayList<String>();
	ArrayList<String> musicName = new ArrayList<String>();
	ArrayList<String> musicArtist = new ArrayList<String>();
	ArrayList<String> musicAlbum = new ArrayList<String>();
	ArrayList<String> musicDurationList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_list);

		Intent intent = getIntent();
		musicList = intent.getStringArrayListExtra("musicList");
		musicName = intent.getStringArrayListExtra("musicName");
		musicArtist = intent.getStringArrayListExtra("musicArtist");
		musicAlbum = intent.getStringArrayListExtra("musicAlbum");
		musicDurationList = intent.getStringArrayListExtra("musicDurationList");

		ListView listView = (ListView) findViewById(R.id.list);
		listAdapter adapter = new listAdapter();
		listView.setAdapter(adapter);
	}

	class listAdapter extends BaseAdapter {

		private String getMusicTime(int time) {
			int seconds = time / 1000;
			int minutes = seconds / 60;
			int overplus = seconds % 60;
			return (minutes > 9 ? minutes : "0" + minutes) + ":"
					+ (overplus > 9 ? overplus : "0" + overplus);
		}

		@Override
		public int getCount() {
			return musicList.size();
		}

		@Override
		public Object getItem(int position) {
			return musicList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater layoutInflater = LayoutInflater
					.from(ListActivity.this);
			RelativeLayout relativeLayout = (RelativeLayout) layoutInflater
					.inflate(R.layout.music_list_item, null);
			TextView title = (TextView) relativeLayout
					.findViewById(R.id.music_list_item_title);
			TextView artist = (TextView) relativeLayout
					.findViewById(R.id.music_list_item_artist);
			ImageButton imageButton = (ImageButton) relativeLayout
					.findViewById(R.id.music_list_item_button);
			title.setText(position + 1 + "、" + musicName.get(position));
			artist.setText(musicArtist.get(position));
			imageButton.setOnClickListener(new OnClickListener() {

				@SuppressLint("SimpleDateFormat")
				@Override
				public void onClick(View v) {
					LinearLayout layout = new LinearLayout(ListActivity.this);
					layout.setOrientation(LinearLayout.VERTICAL);
					File file = new File(musicList.get(position));
					TextView musicInfo = new TextView(ListActivity.this);
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy年MM月dd日 HH:mm:ss");
					musicInfo.setText("歌曲："
							+ musicName.get(position)
							+ "\n歌手："
							+ musicArtist.get(position)
							+ "\n专辑："
							+ musicAlbum.get(position)
							+ "\n路径："
							+ musicList.get(position)
							+ "\n大小："
							+ ((int) (file.length() / 1024.0 / 10.24 + 0.5))
							/ 100.0
							+ "MB\n歌曲时长："
							+ getMusicTime(Integer.parseInt(musicDurationList
									.get(position))) + "\n创建时间："
							+ dateFormat.format(new Date(file.lastModified())));
					layout.setPadding(20, 0, 20, 0);
					layout.addView(musicInfo);
					new AlertDialog.Builder(ListActivity.this,
							AlertDialog.THEME_HOLO_LIGHT)
							.setTitle(musicName.get(position)).setView(layout)
							.create().show();
				}
			});
			relativeLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent service = new Intent();
					service.setAction(Information.FROM_LISTACTIVITY);
					service.putExtra("currentPosition", position);
					sendBroadcast(service);

					Intent intent = new Intent(ListActivity.this,
							PlayActivity.class);
					intent.putExtra("currentPosition", position);
					startActivity(intent);
				}
			});
			return relativeLayout;
		}
	}
}
