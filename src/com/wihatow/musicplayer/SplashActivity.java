package com.wihatow.musicplayer;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;

public class SplashActivity extends Activity {

	ArrayList<String> musicList = new ArrayList<String>();
	ArrayList<String> musicName = new ArrayList<String>();
	ArrayList<String> musicArtist = new ArrayList<String>();
	ArrayList<String> musicAlbum = new ArrayList<String>();
	ArrayList<String> musicDurationList = new ArrayList<String>();
	private Intent intent;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x11) {
				Cursor cursor = getContentResolver().query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
						null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToNext();
					musicList.add(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DATA)));
					musicName.add(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.TITLE)));
					musicArtist.add(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
					musicAlbum.add(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
					musicDurationList.add(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DURATION)));
				}

				Intent service = new Intent(SplashActivity.this,
						PlayService.class);
				service.putStringArrayListExtra("musicList", musicList);
				startService(service);

				intent = new Intent(SplashActivity.this, PlayActivity.class);
				intent.putStringArrayListExtra("musicList", musicList);
				intent.putStringArrayListExtra("musicName", musicName);
				intent.putStringArrayListExtra("musicArtist", musicArtist);
				intent.putStringArrayListExtra("musicAlbum", musicAlbum);
				intent.putStringArrayListExtra("musicDurationList",
						musicDurationList);
				startActivity(intent);
				finish();

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		File testFile = Environment.getExternalStorageDirectory();
		if (!testFile.canWrite()) {
			new AlertDialog.Builder(SplashActivity.this,
					AlertDialog.THEME_HOLO_LIGHT).setTitle("检查SD卡")
					.setMessage("您的SD卡似乎不可用，请检查后再试！").setCancelable(false)
					.setNeutralButton("退出检查", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							finish();
						}
					}).create().show();
		} else {
			handler.sendEmptyMessageDelayed(0x11, 3000);
		}
	}
}
