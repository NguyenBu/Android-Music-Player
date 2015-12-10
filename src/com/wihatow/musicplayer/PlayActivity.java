package com.wihatow.musicplayer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayActivity extends Activity implements OnGestureListener {
	TextView textView;
	ImageButton play;
	ImageButton previous;
	ImageButton next;
	ImageButton playControl;
	ImageButton itemList;
	TextView currentTimeText;
	TextView totalTimeText;
	SeekBar adjustBar;
	Visualizer visualizer;
	int currentRate;
	int isPlay = 2;
	int playMode;
	int currentPosition;
	ArrayList<String> musicList;
	ArrayList<String> musicName;
	ArrayList<String> musicArtist;
	ArrayList<String> musicAlbum;
	ArrayList<String> musicDurationList;
	ShowLyricView lyricView;
	GestureDetector gestureDetector;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x111) {
				setCurrentTime();
				adjustBar.setProgress(currentRate);
				lyricView.setTime(currentRate);
			}
		}
	};

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Information.TO_PLAYACTIVITY)) {
				if (playMode == 3) {
					next();
				} else if (playMode == 1) {
					playRepeat();
				} else if (playMode == 2) {
					random();
				}
			} else if (action.equals(Information.GET_CURRENT_TIME)) {
				currentRate = intent.getIntExtra("currentTime", 0);
				handler.sendEmptyMessage(0x111);
			} else if (action.equals(Information.GET_SESSIONID)) {
				int sessionId = intent.getIntExtra("sessionId", 0);
				if (sessionId != 0 && visualizer == null) {
					visualizer = new Visualizer(sessionId);
					visualizer.setEnabled(false);
					setupVisualizer();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_play);

		gestureDetector = new GestureDetector(this, this);

		lyricView = new ShowLyricView(this);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				(int) (300 * getResources().getDisplayMetrics().density));
		layoutParams.topMargin = ((int) (30 * getResources()
				.getDisplayMetrics().density));
		layoutParams.gravity = Gravity.CENTER;
		addContentView(lyricView, layoutParams);

		Intent intent = getIntent();
		musicList = intent.getStringArrayListExtra("musicList");
		musicName = intent.getStringArrayListExtra("musicName");
		musicArtist = intent.getStringArrayListExtra("musicArtist");
		musicAlbum = intent.getStringArrayListExtra("musicAlbum");
		musicDurationList = intent.getStringArrayListExtra("musicDurationList");
		currentPosition = intent.getIntExtra("currentPosition", 0);

		if (currentPosition == 0) {
			SharedPreferences sharedPreferences = getSharedPreferences(
					"musicData", MODE_PRIVATE);
			currentPosition = sharedPreferences.getInt("currentPosition",
					currentPosition);
			playMode = sharedPreferences.getInt("playMode", playMode);

			Intent toService = new Intent(Information.TO_PLAYSERVICE);
			toService.putExtra("currentPosition", currentPosition);
			sendBroadcast(toService);
		}
		if (musicList.size() == 0) {
			Toast.makeText(this, "还没有歌曲，赶紧去下载吧^_^", Toast.LENGTH_LONG).show();
			return;
		}

		textView = (TextView) findViewById(R.id.textview);
		playControl = (ImageButton) findViewById(R.id.play_mode);
		previous = (ImageButton) findViewById(R.id.previous);
		play = (ImageButton) findViewById(R.id.play);
		next = (ImageButton) findViewById(R.id.next);
		itemList = (ImageButton) findViewById(R.id.item_list);
		currentTimeText = (TextView) findViewById(R.id.current_time);
		totalTimeText = (TextView) findViewById(R.id.total_time);
		adjustBar = (SeekBar) findViewById(R.id.adjust);
		playMode();
		setCurrentTime();
		showMusicInfo();
		playCtrl();
		showNotify();

		IntentFilter filter = new IntentFilter(Information.TO_PLAYACTIVITY);
		filter.addAction(Information.GET_CURRENT_TIME);
		filter.addAction(Information.GET_SESSIONID);
		registerReceiver(receiver, filter);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.play_mode: {
					playMode();
				}
					break;
				case R.id.previous: {
					if (playMode == 2) {
						random();
					} else {
						previous();
					}
				}
					break;
				case R.id.play: {
					playCtrl();
				}
					break;
				case R.id.next: {
					if (playMode == 2) {
						random();
					} else {
						next();
					}
				}
					break;
				case R.id.item_list: {
					Intent intent = new Intent(PlayActivity.this,
							ListActivity.class);
					intent.putStringArrayListExtra("musicList", musicList);
					intent.putStringArrayListExtra("musicName", musicName);
					intent.putStringArrayListExtra("musicArtist", musicArtist);
					intent.putStringArrayListExtra("musicAlbum", musicAlbum);
					intent.putStringArrayListExtra("musicDurationList",
							musicDurationList);
					startActivity(intent);
				}
					break;
				default:
					break;
				}
			}
		};
		playControl.setOnClickListener(listener);
		previous.setOnClickListener(listener);
		play.setOnClickListener(listener);
		next.setOnClickListener(listener);
		itemList.setOnClickListener(listener);
		adjustBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					Intent intent = new Intent(Information.AJDUST_RATE);
					intent.putExtra("currentRate", progress);
					sendBroadcast(intent);
				}
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		currentPosition = intent
				.getIntExtra("currentPosition", currentPosition);
		showMusicInfo();
		showLyric();
		super.onNewIntent(intent);
	}

	@SuppressLint({ "NewApi", "HandlerLeak" })
	private void showNotify() {
		final RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.notification);
		if (isPlay == 2) {
			remoteViews.setImageViewResource(R.id.notify_play, R.drawable.play);
		} else if (isPlay == 1) {
			remoteViews
					.setImageViewResource(R.id.notify_play, R.drawable.pause);
		}
		Intent intent = new Intent(this, SplashActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setTextViewText(R.id.notify_info_text, getMusicInfo());
		final Notification notification = new Notification.Builder(this)
				.setAutoCancel(false).setTicker(getString(R.string.app_name))
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText("一款可以离线播放歌曲的播放器").build();
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.bigContentView = remoteViews;
		notification.contentIntent = pendingIntent;

		final String[] actions = { Information.NOTIFY_PREVIOUS,
				Information.NOTIFY_PLAY, Information.NOTIFY_NEXT };
		BroadcastReceiver notifyReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(actions[0])) {
					if (playMode == 2) {
						random();
					} else {
						previous();
					}
					if (isPlay == 2) {
						remoteViews.setImageViewResource(R.id.notify_play,
								R.drawable.play);
					} else if (isPlay == 1) {
						remoteViews.setImageViewResource(R.id.notify_play,
								R.drawable.pause);
					}
				} else if (action.equals(actions[1])) {
					playCtrl();
					if (isPlay == 2) {
						remoteViews.setImageViewResource(R.id.notify_play,
								R.drawable.play);
					} else if (isPlay == 1) {
						remoteViews.setImageViewResource(R.id.notify_play,
								R.drawable.pause);
					}
				} else if (action.equals(actions[2])) {
					if (playMode == 2) {
						random();
					} else {
						next();
					}
					if (isPlay == 2) {
						remoteViews.setImageViewResource(R.id.notify_play,
								R.drawable.play);
					} else if (isPlay == 1) {
						remoteViews.setImageViewResource(R.id.notify_play,
								R.drawable.pause);
					}
				}
				remoteViews.setTextViewText(R.id.notify_info_text,
						getMusicInfo());
				NotificationManager managers = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				managers.notify(0, notification);
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(actions[0]);
		filter.addAction(actions[1]);
		filter.addAction(actions[2]);
		registerReceiver(notifyReceiver, filter);

		Intent previousIntent = new Intent(actions[0]);
		PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this,
				0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.notify_previous,
				previousPendingIntent);
		Intent playIntent = new Intent(actions[1]);
		PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0,
				playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews
				.setOnClickPendingIntent(R.id.notify_play, playPendingIntent);
		Intent nextIntent = new Intent(actions[2]);
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0,
				nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews
				.setOnClickPendingIntent(R.id.notify_next, nextPendingIntent);
		NotificationManager managers = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		managers.notify(0, notification);
	}

	private void setCurrentTime() {
		int seconds = currentRate / 1000;
		int minutes = seconds / 60;
		int overplus = seconds % 60;
		if (overplus > 60) {
			overplus = 0;
			minutes++;
		}
		currentTimeText.setText((minutes > 9 ? minutes : "0" + minutes) + ":"
				+ (overplus > 9 ? overplus : "0" + overplus));
	}

	private void setTotalTime(int totalRate) {
		int seconds = totalRate / 1000;
		int minutes = seconds / 60;
		int overplus = seconds % 60;
		totalTimeText.setText((minutes > 9 ? minutes : "0" + minutes) + ":"
				+ (overplus > 9 ? overplus : "0" + overplus));
	}

	private void playMode() {
		playMode++;
		if (playMode > 3) {
			playMode = 1;
		}
		if (playMode == 1) {
			playControl.setBackgroundResource(R.drawable.repeat_one);
		} else if (playMode == 2) {
			playControl.setBackgroundResource(R.drawable.random);
		} else if (playMode == 3) {
			playControl.setBackgroundResource(R.drawable.repeat_all);
		}
	}

	void previous() {
		adjustBar.setProgress(0);
		isPlay = 1;
		currentPosition--;
		if (currentPosition < 0) {
			currentPosition = musicList.size() - 1;
		}
		Intent toService = new Intent(Information.TO_PLAYSERVICE);
		toService.putExtra("currentPosition", currentPosition);
		sendBroadcast(toService);
		showMusicInfo();
		playCtrl();
	}

	void next() {
		adjustBar.setProgress(0);
		isPlay = 1;
		currentPosition++;
		if (currentPosition > musicList.size() - 1) {
			currentPosition = 0;
		}
		Intent toService = new Intent(Information.TO_PLAYSERVICE);
		toService.putExtra("currentPosition", currentPosition);
		sendBroadcast(toService);
		showMusicInfo();
		playCtrl();
	}

	void random() {
		adjustBar.setProgress(0);
		currentPosition = (int) (Math.random() * musicList.size() + 0.5);
		if (currentPosition > musicList.size() - 1) {
			currentPosition = 0;
		} else if (currentPosition < 0) {
			currentPosition = musicList.size() - 1;
		}
		Intent toService = new Intent(Information.TO_PLAYSERVICE);
		toService.putExtra("currentPosition", currentPosition);
		sendBroadcast(toService);
		isPlay = 1;
		showMusicInfo();
		playCtrl();
	}

	private void playRepeat() {
		adjustBar.setProgress(0);
		Intent toService = new Intent(Information.TO_PLAYSERVICE);
		toService.putExtra("currentPosition", currentPosition);
		sendBroadcast(toService);
		isPlay = 1;
		showMusicInfo();
		playCtrl();
	}

	String getMusicInfo() {
		return "歌曲：" + musicName.get(currentPosition) + "\n歌手："
				+ musicArtist.get(currentPosition) + "\n专辑："
				+ musicAlbum.get(currentPosition);
	}

	void playCtrl() {
		int totalTime = Integer
				.parseInt(musicDurationList.get(currentPosition));
		adjustBar.setMax(totalTime);
		setTotalTime(totalTime);

		Intent intent = new Intent();
		intent.setAction(Information.PLAY_PAUSE);
		if (isPlay == 2) {
			play.setBackgroundResource(R.drawable.pause);
			isPlay = 1;
			lyricView.setIsPause(true);
			intent.putExtra("play_state", 0);
			sendBroadcast(intent);
		} else if (isPlay == 1) {
			play.setBackgroundResource(R.drawable.play);
			isPlay = 2;
			lyricView.setIsPause(false);
			intent.putExtra("play_state", 1);
			sendBroadcast(intent);
		}
		showLyric();
	}

	private void showMusicInfo() {
		textView.setText(getMusicInfo());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuitem_about) {
			TextView textView = new TextView(PlayActivity.this);
			textView.setTextSize(20);
			textView.setPadding(20, 0, 20, 0);
			textView.setText(R.string.about_info);
			ScrollView scrollView = new ScrollView(PlayActivity.this);
			scrollView.addView(textView);
			new AlertDialog.Builder(PlayActivity.this,
					AlertDialog.THEME_HOLO_LIGHT)
					.setTitle(R.string.about)
					.setView(scrollView)
					.setCancelable(false)
					.setNeutralButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).create().show();
		} else if (item.getItemId() == R.id.menuitem_exit) {
			NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			manager.cancel(0);

			Intent intent = new Intent(this, PlayService.class);
			stopService(intent);
			finish();
		}
		return true;
	}

	private File[] getLyrics() {
		File file = new File(Uri.decode(musicList.get(currentPosition)))
				.getParentFile();
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				String path = pathname.getAbsolutePath();
				if (path.endsWith(".lrc") || path.endsWith(".trc")) {
					return true;
				} else {
					return false;
				}
			}
		};
		File lyricList[] = file.listFiles(filter);
		return lyricList;
	}

	private void showLyric() {
		File lyricList[] = getLyrics();
		ReadLyric readLyric = null;
		lyricView.clear();
		for (int i = 0; i < lyricList.length; i++) {
			readLyric = new ReadLyric(lyricList[i].getAbsolutePath()
					.replaceAll("%20", " "));
			if ((lyricList[i]
					.getName()
					.replaceAll("%20", "")
					.equals((musicName.get(currentPosition) + ".lrc")
							.replaceAll("%20", "")) || lyricList[i]
					.getName()
					.replaceAll("%20", "")
					.equals((musicName.get(currentPosition) + ".trc")
							.replaceAll("%20", "")))
					|| (readLyric.getTitle() != null && musicName
							.get(currentPosition).replaceAll("%20", "")
							.equals(readLyric.getTitle().replaceAll("%20", "")))) {
				lyricView.clear();
				lyricView.setTimeList(readLyric.getSingleTime());
				lyricView.setLyricList(readLyric.getSingleLyric());
				lyricView.setTotalTime(Long.parseLong(musicDurationList
						.get(currentPosition)));
				lyricView.refresh();
				lyricView.invalidate();
				return;
			}
		}
	}

	private void setupVisualizer() {
		if (visualizer != null) {
			final VisualizerView visualizerView = new VisualizerView(this);
			visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					(int) (180 * getResources().getDisplayMetrics().density));
			layoutParams.gravity = Gravity.TOP;
			layoutParams.setMargins(15, 0, 15, 0);
			addContentView(visualizerView, layoutParams);
			visualizer.setDataCaptureListener(new OnDataCaptureListener() {
				@Override
				public void onFftDataCapture(Visualizer visualizer, byte[] fft,
						int samplingRate) {
					byte[] data = new byte[fft.length / 2 + 1];
					data[0] = (byte) Math.abs(fft[1]);
					for (int i = 2, j = 1; i < fft.length - 1; i += 2, j++) {
						data[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
					}
					visualizerView.updateVisualizer(data);
				}

				@Override
				public void onWaveFormDataCapture(Visualizer visualizer,
						byte[] waveform, int samplingRate) {
					visualizerView.updateVisualizer(waveform);
				}
			}, Visualizer.getMaxCaptureRate(), true, false);
			visualizer.setEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		SharedPreferences sharedPreferences = getSharedPreferences("musicData",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("currentPosition", currentPosition);
		editor.putInt("playMode", playMode - 1);
		editor.commit();
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Intent intent = new Intent(this, CategoryActivity.class);
		intent.putStringArrayListExtra("musicList", musicList);
		intent.putStringArrayListExtra("musicName", musicName);
		intent.putStringArrayListExtra("musicArtist", musicArtist);
		intent.putStringArrayListExtra("musicAlbum", musicAlbum);
		startActivity(intent);
		if (e1.getX() - e2.getX() > 50) {
			overridePendingTransition(R.anim.enter_music_menu_from_right,
					R.anim.exit_music_menu_to_left);
		} else if (e2.getX() - e1.getX() > 50) {
			overridePendingTransition(R.anim.enter_music_menu_from_left,
					R.anim.exit_music_menu_to_right);
		} else if (e1.getY() - e2.getY() > 50) {
			overridePendingTransition(R.anim.enter_music_menu_from_bottom,
					R.anim.exit_music_menu_to_top);
		} else if (e2.getY() - e1.getY() > 50) {
			overridePendingTransition(R.anim.enter_music_menu_from_top,
					R.anim.exit_music_menu_to_bottom);
		}
		return false;
	}
}
