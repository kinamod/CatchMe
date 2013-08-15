package com.kinamod.catchme2.gameobject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.kinamod.catchme2.util.CustomisedLogging;

public class Explosion {
	private static final CustomisedLogging logger = new CustomisedLogging(false, false);
	private int colour;
	private float currentDiam = 20;
	private final Bitmap explosionBitmap;
	private boolean explosionFinished;
	private final RectF rect;
	private boolean started = false;
	private int transparency;

	// public Explosion(int colIn, PointF posIn, PointF speed) {
	// colour = colIn;
	// position = new PointF(posIn.x, posIn.y);
	// velocity = new PointF(-(speed.x / 3), -(speed.y / 3));
	// }

	public Explosion(Bitmap bmpIn, float posX, float posY) {
		explosionBitmap = Bitmap.createBitmap(bmpIn);
		rect = new RectF(posX - currentDiam / 2, posY - currentDiam / 2, posX + currentDiam / 2, posY + currentDiam / 2);
		logger.localDebugLog(2, "createExplosion", explosionBitmap.toString() + " : " + bmpIn.toString());
	}

	public void drawExplosion(Canvas canvas, Paint mPaint) {
		logger.localDebugLog(2, "createExplosion", "just before scale: " + explosionBitmap.toString());
		// explosionBitmap = Bitmap.createScaledBitmap(explosionBitmap, (int)
		// currentRadius,
		// (int) currentRadius, false);
		mPaint.setAlpha((int) (255 / 50 * currentDiam));

		logger.localDebugLog(2, "createExplosion", "just before draw: " + explosionBitmap.toString() + "\nmPaint: "
				+ mPaint);
		// canvas.drawBitmap(explosionBitmap, position.x - currentRadius / 2,
		// position.y - currentRadius / 2,
		// mPaint);
		canvas.drawBitmap(explosionBitmap, null, rect, mPaint);
	}

	public int getColour() {
		return colour;
	}

	public float getCurrentRadius() {
		return currentDiam;
	}

	public Bitmap getExplosionBitmap() {
		return explosionBitmap;
	}

	public int getTransparency() {
		return transparency;
	}

	public boolean isExplosionFinished() {
		return explosionFinished;
	}

	public boolean isStarted() {
		return started;
	}

	public void started() {
		started = true;
	}

	public void startExplosion() {
		final Thread meExplode = new Thread("meExplodeThread") {
			@Override
			public void run() {
				while (currentDiam < 50) {
					update(.01f);
				}
				explosionFinished = true;
			}
		};
		meExplode.start();
	}

	public void update(float dTime) {
		currentDiam += dTime;
		logger.localDebugLog(1, "updateExplosion", "currentRadius: " + currentDiam);
	}
}
