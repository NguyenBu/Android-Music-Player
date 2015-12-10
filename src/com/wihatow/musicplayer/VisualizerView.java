package com.wihatow.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class VisualizerView extends View {
	private byte[] bytes;
	private float[] points;
	private Paint paint = new Paint();
	private byte type = 1;

	public VisualizerView(Context context) {
		super(context);
		bytes = null;
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.FILL);
	}

	public void updateVisualizer(byte[] waveform) {
		bytes = waveform;
		invalidate();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}
		type++;
		if (type >= 3) {
			type = 0;
		}
		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (bytes == null) {
			return;
		}
		canvas.drawColor(Color.TRANSPARENT);
		float tem = getHeight() / 2;
		float left, top, right, bottom;
		switch (type) {
		// -------绘制块状的波形图-------
		case 0:
			for (int i = 0; i < bytes.length - 1; i++) {
				left = getWidth() * i / (bytes.length - 1);
				top = (tem) + ((byte) (bytes[i] + 128));
				right = left + 1;
				bottom = getHeight();
				canvas.drawRect(left, top, right, bottom, paint);
			}
			break;
		// -------绘制柱状的波形图（每隔15个抽样点绘制一个矩形）-------
		case 1:
			for (int i = 0; i < bytes.length - 1; i += 15) {
				left = getWidth() * i / (bytes.length - 1);
				top = (tem) + ((byte) (bytes[i] + 128));
				right = left + 5;// 宽度
				bottom = getHeight();
				canvas.drawRect(left, top, right, bottom, paint);
			}
			break;
		// -------绘制曲线波形图-------
		case 2:
			if (points == null || points.length < bytes.length * 4) {
				points = new float[bytes.length * 4];
			}
			for (int i = 0; i < bytes.length - 1; i++) {
				points[i * 4] = getWidth() * i / (bytes.length - 1);
				points[i * 4 + 1] = (tem) + ((byte) (bytes[i] + 128));
				points[i * 4 + 2] = getWidth() * (i + 1) / (bytes.length - 1);
				points[i * 4 + 3] = (tem) + ((byte) (bytes[i + 1] + 128));
			}
			canvas.drawLines(points, paint);
			break;
		}
	}
}