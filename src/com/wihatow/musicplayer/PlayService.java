package com.wihatow.musicplayer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PlayService extends Service implements OnPreparedListener,
		OnCompletionListener, OnErrorListener {
	MediaPlayer player;
	boolean canPlay = false;
	boolean isRemote = true;
	int currentPosition;
	ArrayList<String> musicList;
	TelephonyManager telephonyManager;
	PhoneStateListener listener = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_RINGING:
				if (player.isPlaying()) {
					player.pause();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (!player.isPlaying()) {
					player.start();
				}
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	};

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Information.TO_PLAYSERVICE)
					|| action.equals(Information.FROM_LISTACTIVITY)) {
				isRemote = false;
				canPlay = true;
				currentPosition = intent.getIntExtra("currentPosition",
						currentPosition);
				String path = musicList.get(currentPosition);
				playMusic(path);
			} else if (action.equals(Information.PLAY_PAUSE)) {
				int playState = intent.getIntExtra("play_state", 0);
				sendSessionId();
				if (playState == 1) {
					canPlay = true;
					if (!player.isPlaying()) {
						player.start();
					}
				} else if (playState == 0) {
					canPlay = false;
					if (player.isPlaying()) {
						player.pause();
					}
				}
			} else if (action.equals(Information.AJDUST_RATE)) {
				player.seekTo(intent.getIntExtra("currentRate", 0));
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		musicList = intent.getStringArrayListExtra("musicList");
		return super.onStartCommand(intent, flags, startId);
	}

	private void playMusic(String path) {
		player.reset();
		try {
			player.setDataSource(path);
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.prepareAsync();
		} catch (Exception e) {
		}
	}

	@Override
	public void onCreate() {
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		player = new MediaPlayer();
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Information.TO_PLAYSERVICE);
		filter.addAction(Information.PLAY_PAUSE);
		filter.addAction(Information.AJDUST_RATE);
		filter.addAction(Information.FROM_LISTACTIVITY);
		registerReceiver(receiver, filter);
		final Intent intent = new Intent(Information.GET_CURRENT_TIME);
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (player.isPlaying()) {
					int currentRate = player.getCurrentPosition();
					intent.putExtra("currentTime", currentRate);
					sendBroadcast(intent);
				}
			}
		};
		timer.schedule(task, 0, 10);
	}

	private void sendSessionId() {
		Intent intent2 = new Intent(Information.GET_SESSIONID);
		intent2.putExtra("sessionId", player.getAudioSessionId());
		sendBroadcast(intent2);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		isRemote = true;
		if (canPlay) {
			mp.start();
		}
		canPlay = false;
		sendSessionId();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Intent intent = new Intent(Information.TO_PLAYACTIVITY);
		sendBroadcast(intent);
		if (isRemote && musicList != null) {
			currentPosition++;
			if (currentPosition > musicList.size() - 1) {
				currentPosition = 0;
			}
			playMusic(musicList.get(currentPosition));
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		if (player.isPlaying()) {
			player.stop();
		}
		player.release();
		stopSelf();
		super.onDestroy();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		playMusic(musicList.get(currentPosition));
		return true;
	}
}
