package com.wihatow.musicplayer;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CategoryActivity extends Activity {

	ArrayList<String> musicList;
	ArrayList<String> musicName;
	ArrayList<String> musicArtist;
	ArrayList<String> musicAlbum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		Intent intent = getIntent();
		musicList = intent.getStringArrayListExtra("musicList");
		musicName = intent.getStringArrayListExtra("musicName");
		musicArtist = intent.getStringArrayListExtra("musicArtist");
		musicAlbum = intent.getStringArrayListExtra("musicAlbum");
		Button all = (Button) findViewById(R.id.all_list_button);
		Button album = (Button) findViewById(R.id.album_list_button);
		Button artist = (Button) findViewById(R.id.artist_list_button);
		Button folder = (Button) findViewById(R.id.folder_list_button);
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.all_list_button: {
					Intent intent = new Intent(CategoryActivity.this,
							TotalActivity.class);
					intent.putStringArrayListExtra("musicList", musicList);
					intent.putStringArrayListExtra("musicName", musicName);
					startActivity(intent);
				}
					break;
				case R.id.artist_list_button: {
					Intent intent = new Intent(CategoryActivity.this,
							ArtistActivity.class);
					intent.putStringArrayListExtra("musicList", musicList);
					intent.putStringArrayListExtra("musicArtist", musicArtist);
					intent.putStringArrayListExtra("musicName", musicName);
					intent.putStringArrayListExtra("musicAlbum", musicAlbum);
					startActivity(intent);
				}
					break;
				case R.id.album_list_button: {
					Intent intent = new Intent(CategoryActivity.this,
							AlbumActivity.class);
					intent.putStringArrayListExtra("musicList", musicList);
					intent.putStringArrayListExtra("musicAlbum", musicAlbum);
					intent.putStringArrayListExtra("musicName", musicName);
					startActivity(intent);
				}
					break;
				case R.id.folder_list_button: {
					Intent intent = new Intent(CategoryActivity.this,
							FolderActivity.class);
					intent.putStringArrayListExtra("musicList", musicList);
					intent.putStringArrayListExtra("musicName", musicName);
					startActivity(intent);
				}
					break;
				default:
					break;
				}
			}
		};
		all.setOnClickListener(listener);
		artist.setOnClickListener(listener);
		album.setOnClickListener(listener);
		folder.setOnClickListener(listener);
	}
}
