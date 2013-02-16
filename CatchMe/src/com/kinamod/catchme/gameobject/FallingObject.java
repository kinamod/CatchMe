/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.gameobject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

import com.kinamod.catchme.util.CustomisedLogging;

public class FallingObject {
	private final int colour;
	private boolean dying = false;
	private boolean killMe = false;
	CustomisedLogging logger = new CustomisedLogging(false, false);
	Bitmap myBitmap;
	PointF position = new PointF();
	PointF speed = new PointF();

	@SuppressLint("UseSparseArrays")
	public FallingObject(int timeToCentre, Point startPosition, Point sizeIn, Bitmap bmpIN, int colourIN) {
		final String TAG = "newFallingObject";
		float fromX, fromY;
		colour = colourIN;
		final Point centerPoint = new Point(sizeIn.x / 2, sizeIn.y / 2);
		fromX = centerPoint.x - startPosition.x;
		fromY = centerPoint.y - startPosition.y;

		speed.set(fromX / timeToCentre, fromY / timeToCentre);
		logger.localDebugLog(1, TAG, "Speed: " + speed.x + ":" + speed.y);
		logger.localDebugLog(1, TAG, "Start: " + startPosition.x + ":" + startPosition.y);
		position.set(startPosition.x, startPosition.y);
		myBitmap = bmpIN;
	}

	public void drawCircle(Canvas canvas, Paint mPaint, int circleSize) {
		final String TAG = "drawCircle";
		mPaint.setColor(colour);
		final float x = position.x - circleSize, y = position.y - circleSize;
		logger.localDebugLog(2, TAG, "Circle Being Drawn: " + myBitmap.toString() + " : x: " + x + ", y: " + y);
		canvas.drawBitmap(myBitmap, x, y, mPaint);
	}

	public float fromCentre(Point centerPoint) {
		final float fromCentre = (float) Math.hypot(position.x - centerPoint.x, position.y - centerPoint.y);
		return fromCentre;
	}

	public Bitmap getBitmap() {
		return myBitmap;
	}

	public int getColour() {
		return colour;
	}

	public synchronized boolean getKillMe() {
		return killMe;
	}

	public PointF getPosition() {
		return new PointF(position.x, position.y);
	}

	private void killMe() {
		killMe = true;
	}

	private void sendOffScreen() {
		position.set(-100, -100);
	}

	public void setDying(boolean dying) {
		this.dying = dying;
	}

	public boolean shouldICheck(int halfBucket, int circleSize, Point centerPoint) {
		if (!dying) {
			if (fromCentre(centerPoint) < Math.hypot(halfBucket, halfBucket) + circleSize) {
				return true;
			}
		}
		return false;
	}

	private void shrinkAndKill() {
		if (!killMe) {
			sendOffScreen();
			killMe();
		}
	}

	public void updatePosition(float dTime) {
		final String TAG = "FallingObject.UpdatePosition";

		logger.localDebugLog(1, TAG, "PositionBefore:\n    " + position.x + " : " + position.y + " - speed: " + speed.x
				+ " : " + speed.y);

		position.set(position.x + speed.x * dTime, position.y + speed.y * dTime);

		logger.localDebugLog(1, TAG, "PositionAfter: " + position.x + " : " + position.y + " - dTime: " + dTime + "\n");

		if (dying) {
			shrinkAndKill();
		}
	}
}
