/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.SurfaceView;

import com.kinamod.catchme.CatchMe;
import com.kinamod.catchme.R;
import com.kinamod.catchme.activities.MainGameActivity;

public class GameCanvas extends SurfaceView implements SensorEventListener {

	private static CustomisedLogging logger = new CustomisedLogging(false,
			false);

	CatchMe catchMe = CatchMe.getInstance();
	MainGameActivity mainGameActivity;
	private Paint mPaint = new Paint();
	// TESTING ROTATION
	int of360 = 0;
	String scoreString = "", highScoreString = "";

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
		final String TAG = "onDraw";
		mPaint.setColor(Color.rgb(200, 200, 200));
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
		drawCircles(canvas);
		// invalidate();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		final String TAG = "sensorChanged";

		float[] values = event.values;
		logger.localDebugLog(1, TAG, "values.length: " + values.length);
		// Movement
		float orientX = values[0];
		float orientY = values[1];

		if (orientY < 0) {
			catchMe.setRotateDegrees(MathsHelper.xAndYtoDegrees(orientX,
					orientY) + 180);
		} else {
			catchMe.setRotateDegrees(MathsHelper.xAndYtoDegrees(orientX,
					orientY));
		}
	}
}