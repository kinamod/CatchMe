package com.kinamod.catchme.containers;

import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.kinamod.catchme.gameobject.Explosion;
import com.kinamod.catchme.util.CustomisedLogging;

public class ExplosionContainer {
	private static final CustomisedLogging logger = new CustomisedLogging(false, false);
	private final CopyOnWriteArrayList<Explosion> explosions = new CopyOnWriteArrayList<Explosion>();

	public void addExplosion(Bitmap myBitmap, PointF pos) {
		explosions.add(new Explosion(myBitmap, pos.x, pos.y));
	}

	// public void addExplosion(Explosion expIn) {
	// explosions.add(expIn);
	// }

	public void drawExplosions(Canvas canvas, Paint mPaint) {
		startExplosions(canvas, mPaint);
		for (final Explosion thisOne : explosions) {
			thisOne.drawExplosion(canvas, mPaint);
		}
	}

	public boolean isEmpty() {
		return explosions.isEmpty();
	}

	public void killFinishedExplosions() {
		for (int i = 0; i < size(); i++) {
			if (explosions.get(i).isExplosionFinished()) {
				logger.localDebugLog(2, "noExplosions", "explosionKilled");
				explosions.remove(i);
			}
		}
	}

	// public void updateExplosion(float deltaMilli) {
	// float dTimeShrink = deltaMilli / 20;
	// for (Explosion thisOne : explosions) {
	// thisOne.update(dTimeShrink);
	// }
	// }

	public int size() {
		return explosions.size();
	}

	public void startExplosions(Canvas canvas, Paint mPaint) {
		if (!explosions.isEmpty()) {
			for (int i = 0; i < explosions.size(); i++) {
				if (!explosions.get(i).isStarted()) {
					explosions.get(i).startExplosion();
					explosions.get(i).started();
				} else if (explosions.get(i).isExplosionFinished()) {
					explosions.remove(i);
				}
			}
		}
	}
}