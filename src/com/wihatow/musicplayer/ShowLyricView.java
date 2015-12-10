package com.wihatow.musicplayer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;

public class ShowLyricView extends View {
	ArrayList<Long> singleTimeList;
	ArrayList<String> singleLyricList;
	Paint paint = new Paint();
	Paint currentPaint = new Paint();
	int index;
	float autoPlus;
	boolean isPause = false;
	long time;
	long totalTime;
	TimeThread timeThread;
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!isPause) {
				autoPlus += 0.1f;
			}
			invalidate();
		}
	};

	Handler handler = new Handler();
	private int maxLength;

	public class TimeThread extends Thread {

		@Override
		public void run() {
			long sleepTime;
			time = 0;
			while (time <= totalTime) {
				index = getIndex(time);
				if (autoPlus > 20f) {
					autoPlus = 0;
				}
				if (singleTimeList == null || index > singleTimeList.size() - 1
						|| index == -1) {
					return;
				}
				sleepTime = singleTimeList.get(index + 1)
						- singleTimeList.get(index);
				try {
					if (sleepTime > 0) {
						sleep(sleepTime);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ShowLyricView(Context context) {
		super(context);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		currentPaint.setColor(Color.YELLOW);
		currentPaint.setTextAlign(Paint.Align.CENTER);
	}

	public void clear() {
		singleTimeList = null;
		singleLyricList = null;
		time = 0;
		totalTime = 0;
		timeThread = null;
		invalidate();
	}

	public void refresh() {
		maxLength = 0;
		invalidate();
		for (int i = 0; i < singleLyricList.size(); i++) {
			if (maxLength < singleLyricList.get(i).length()) {
				maxLength = singleLyricList.get(i).length();
			}
		}
		if (singleTimeList != null) {
			timeThread = new TimeThread();
			timeThread.start();
		}
	}

	public void setIsPause(boolean isPause) {
		this.isPause = isPause;
	}

	public void setTimeList(ArrayList<Long> singleTimeList) {
		this.singleTimeList = singleTimeList;
	}

	public void setLyricList(ArrayList<String> singleLyricList) {
		this.singleLyricList = singleLyricList;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
		invalidate();
	}

	public void setTime(long time) {
		this.time = time;
		invalidate();
	}

	private int getIndex(long time) {
		int indexs = -1;
		if (singleTimeList == null) {
			return indexs;
		}
		for (int i = 0; i < singleTimeList.size() - 1; i++) {
			if (singleTimeList.get(i) <= time
					&& time < singleTimeList.get(i + 1)) {
				indexs = i;
				break;
			} else if (time < singleTimeList.get(0)) {
				indexs = 0;
			}
		}
		return indexs;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float middelY = getHeight() / 2;
		float middelX = getWidth() / 2;
		canvas.drawColor(Color.TRANSPARENT);
		if (maxLength != 0) {
			float fontSize = getWidth() / maxLength;
			if (fontSize < 16f) {
				fontSize = 16f;
			}
			paint.setTextSize(fontSize - 5);
			currentPaint.setTextSize(fontSize);
		}
		try {
			float temY;
			if (index > singleLyricList.size() - 1) {
				return;
			}
			canvas.drawText(singleLyricList.get(index), middelX, middelY
					- autoPlus, currentPaint);
			temY = middelY - autoPlus;
			for (int i = index - 1; i >= 0; i--) {
				temY -= 50;
				if (temY < 0) {
					break;
				}
				canvas.drawText(singleLyricList.get(i), middelX, temY, paint);
			}
			temY = middelY - autoPlus;
			for (int j = index + 1; j < singleLyricList.size(); j++) {
				temY += 50;
				if (temY > 2 * middelY) {
					break;
				}
				canvas.drawText(singleLyricList.get(j), middelX, temY, paint);
			}
		} catch (Exception e) {
			currentPaint.setTextSize(30);
			canvas.drawText("没有找到歌词哦-_-", middelX, middelY, currentPaint);
		}
		handler.post(runnable);
		super.onDraw(canvas);
	}
}
