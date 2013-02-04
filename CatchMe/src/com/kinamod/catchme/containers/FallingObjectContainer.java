package com.kinamod.catchme.containers;

import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.SparseArray;

import com.kinamod.catchme.CatchMe;
import com.kinamod.catchme.R;
import com.kinamod.catchme.activities.MainGameActivity;
import com.kinamod.catchme.gameobject.FallingObject;
import com.kinamod.catchme.util.CustomisedLogging;

public class FallingObjectContainer {
	static SparseArray<Bitmap> circles = new SparseArray<Bitmap>(5);
	private static int circleSize = 10;
	private static final CustomisedLogging logger = new CustomisedLogging(
			false, false);

	public static void releaseBitMaps() {
		circles.clear();
	}

	public static void setUpBitMaps(MainGameActivity mainGameActivity) {
		circles.put(Color.RED, Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(mainGameActivity.getResources(),
						R.drawable.redball), circleSize * 2, circleSize * 2,
				false));

		circles.put(Color.BLUE, Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(mainGameActivity.getResources(),
						R.drawable.blueball), circleSize * 2, circleSize * 2,
				false));

		circles.put(Color.GREEN, Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(mainGameActivity.getResources(),
						R.drawable.greenball), circleSize * 2, circleSize * 2,
				false));

		circles.put(Color.YELLOW, Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(mainGameActivity.getResources(),
						R.drawable.yellowball), circleSize * 2, circleSize * 2,
				false));

		circles.put(Color.MAGENTA, Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(mainGameActivity.getResources(),
						R.drawable.pinkball), circleSize * 2, circleSize * 2,
				false));
	}

	CatchMe catchMe = CatchMe.getInstance();

	private CopyOnWriteArrayList<FallingObject> fallingCircles = new CopyOnWriteArrayList<FallingObject>();

	public FallingObjectContainer(MainGameActivity mainGameActivity) {
		setUpBitMaps(mainGameActivity);
	}

	public void drawCircles(Canvas canvas, Paint mPaint) {
		@SuppressWarnings("unused")
		final String TAG = "drawCircles";
		if (!fallingCircles.isEmpty()) {
			for (FallingObject itCircle : fallingCircles) {
				itCircle.drawCircle(canvas, mPaint, circleSize);
			}
		}
	}

	private int generateColour() {
		int colours = 5;
		int rand = (int) (Math.random() * colours);
		logger.localDebugLog(1, "circleColour", "number: " + rand + " : "
				+ colours);
		switch (rand) {
		case 0:
			return Color.RED;
		case 1:
			return Color.BLUE;
		case 2:
			return Color.GREEN;
		case 3:
			return Color.YELLOW;
		default:
			return Color.MAGENTA;
		}
	}

	public FallingObject get(int i) {
		return fallingCircles.get(i);
	}

	public float getCircleSize() {
		return circleSize;
	}

	public void killDeadCircles() {
		for (int i = 0; i < size(); i++) {
			if (fallingCircles.get(i).getKillMe()) {
				logger.localDebugLog(2, "noCircles", "circleKilled");
				fallingCircles.remove(i);
			}
		}
	}

	public void makeNewFallingObject(int timeToCentre, Point startPosition,
			Point sizeIn) {
		@SuppressWarnings("unused")
		final String TAG = "makeNewFallingObject";
		int color = generateColour();
		fallingCircles.add(new FallingObject(timeToCentre, startPosition,
				sizeIn, circles.get(color), color));
	}

	public boolean shouldICheck(int i, int halfBucket) {
		return get(i).shouldICheck(halfBucket, circleSize,
				catchMe.getCentrePoint());
	}

	public int size() {
		return fallingCircles.size();
	}

	public void updateCirclePositions(float deltaMilli) {
		float dTimeShrink = deltaMilli / 20;
		for (FallingObject thisOne : fallingCircles) {
			thisOne.updatePosition(dTimeShrink);
		}
	}
}
