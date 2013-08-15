/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme2.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.SurfaceView;

import com.kinamod.catchme2.CatchMe;
import com.kinamod.catchme2.R;
import com.kinamod.catchme2.activities.MainGameActivity;

public class GameCanvas extends SurfaceView implements SensorEventListener {

	private static CustomisedLogging logger = new CustomisedLogging(false,
			false);

	CatchMe catchMe = CatchMe.getInstance();
	MainGameActivity mainGameActivity;
	private final Paint mPaint = new Paint();
	// TESTING ROTATION
	int of360 = 0;
	float textSize = 50, incer = 1;
	String scoreString = "", highScoreString = "";
	Canvas thisCanvas;

	public GameCanvas(Context context) {
		super(context);
	}

	public GameCanvas(MainGameActivity activity, Context context) {
		super(context);
		mainGameActivity = activity;
		scoreString = getResources().getString(R.string.stringScore);
		highScoreString = getResources().getString(R.string.stringHighScore);

		this.setWillNotDraw(false);
		makePaint();
	}

	private void drawBackgroundStars(Canvas canvas) {
		mainGameActivity.getBgStars().drawBackgroundStars(canvas);

	}

	private void drawBars(Canvas canvas) {
		mainGameActivity.getFuelBar().drawBar(canvas, mPaint);
		mainGameActivity.getHealthBar().drawBar(canvas, mPaint);
	}

	private void drawBucket(Canvas canvas) {
		mainGameActivity.bucket.drawBucket(canvas, catchMe.getScreenSize());
	}

	private void drawCircles(Canvas canvas) {
		mainGameActivity.getFOContainer().drawCircles(canvas, mPaint);
	}

	private void drawExplosions(Canvas canvas) {
		mainGameActivity.getExplosionContainer().drawExplosions(canvas, mPaint);
	}

	private void makePaint() {
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(40);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDraw(Canvas canvas) {

	}

	private void drawEverything() {
		final Canvas canvas = getHolder().lockCanvas();
		if (canvas == null) {
			return;// TODO
		}
		final String TAG = "onDraw";
		mPaint.setColor(Color.rgb(200, 200, 200));
		canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
		drawBackgroundStars(canvas);

		// if (catchMe.getMultiplier() > 1) {
		// canvas.drawText(scoreString + catchMe.getScore() + " x " +
		// catchMe.getMultiplier(), 0, 50, mPaint);
		// } else {
		canvas.drawText(scoreString + catchMe.getScore(), 0, 50, mPaint);
		// }
		if (catchMe.getScore() > catchMe.getHighScore()) {
			canvas.drawText(highScoreString + catchMe.getScore(), 240, 50,
					mPaint);
		} else {
			canvas.drawText(highScoreString + catchMe.getHighScore(), 240, 50,
					mPaint);
		}

		/*
		 * TESTING CONTINUOUS ROTATION
		 */
		// of360 += 2;
		// if (of360 > 360) {
		// of360 = 0;
		// }
		// GameState.setRotateDegrees(of360);
		/*
		 * END TESTING ROTATION bucket.drawBucket(canvas,
		 */
		drawBucket(canvas);
		logger.localDebugLog(2, TAG, "onDraw");
		drawExplosions(canvas);
		drawBars(canvas);
		if (!catchMe.isPaused()) {
			drawCircles(canvas);
		}
		if (catchMe.isGameStopped() || catchMe.isPaused()) {
			final float beforeSize = mPaint.getTextSize();
			mPaint.setColor(Color.rgb(200, 200, 200));
			if (textSize > 60 || textSize < 40) {
				incer *= -1;
			}
			textSize += incer;
			mPaint.setTextSize(textSize);
			mPaint.setTextAlign(Paint.Align.CENTER);
			if (catchMe.isPaused()) {
				canvas.drawText(
						mainGameActivity.getResources().getString(
								R.string.textTouchToResume),
						catchMe.getScreenSize().x / 2,
						catchMe.getScreenSize().x / 2, mPaint);
				mPaint.setTextSize(30);
				canvas.drawText(
						mainGameActivity.getResources().getString(
								R.string.textBackToEndGame),
						catchMe.getScreenSize().x / 2,
						catchMe.getScreenSize().y / 2, mPaint);
			} else {
				canvas.drawText(
						mainGameActivity.getResources().getString(
								R.string.textTapToStart),
						catchMe.getScreenSize().x / 2,
						catchMe.getScreenSize().x / 2, mPaint);
			}
			mPaint.setTextSize(beforeSize);
			mPaint.setTextAlign(Paint.Align.LEFT);
		}
		// invalidate();
		getHolder().unlockCanvasAndPost(canvas);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		final String TAG = "sensorChanged";

		final float[] values = event.values;
		logger.localDebugLog(1, TAG, "values.length: " + values.length);
		// Movement
		final float orientX = values[0];
		final float orientY = values[1];

		if (orientY < 0) {
			catchMe.setRotateDegrees(MathsHelper.xAndYtoDegrees(orientX,
					orientY) + 180);
		} else {
			catchMe.setRotateDegrees(MathsHelper.xAndYtoDegrees(orientX,
					orientY));
		}
	}

	public void requestDraw() {
		// onDraw(canvas);
		// invalidate();
		drawEverything();
	}
}