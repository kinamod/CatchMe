/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme2.gameobject;

import android.graphics.Color;
import android.graphics.PointF;

import com.kinamod.catchme2.util.CustomisedLogging;

public class BucketLine {
	private static CustomisedLogging logger = new CustomisedLogging(false, false);
	PointF aye, bee;
	PointF ayeOrig, beeOrig;
	int color;

	public BucketLine(int colIn, PointF start, PointF end) {
		ayeOrig = start;
		beeOrig = end;
		aye = new PointF(ayeOrig.x, ayeOrig.y);
		bee = new PointF(beeOrig.x, beeOrig.y);
		color = colIn;
	}

	public PointF getAye() {
		return aye;
	}

	public PointF getBee() {
		return bee;
	}

	public int getColor() {
		return color;
	}

	public float[] getPointsInFloatArray() {
		return new float[] { aye.x, aye.y, bee.x, bee.y };
	}

	public void rotateLine(double radius, double deg) {
		final String TAG = "rotateLine";
		int tagSwitch = 0;
		if (color == Color.RED) {
			tagSwitch = 2;
		} else {
			tagSwitch = 1;
		}
		logger.localDebugLog(tagSwitch, TAG, "degrees: " + deg + color);

		double ax, ay, bx, by;
		final double theta = Math.toRadians(deg);
		// for top left corner
		ax = ayeOrig.x * Math.cos(theta) - ayeOrig.y * Math.sin(theta);
		ay = ayeOrig.x * Math.sin(theta) + ayeOrig.y * Math.cos(theta);
		aye.set((float) ax, (float) ay);

		logger.localDebugLog(tagSwitch, TAG, "aye - dx:dy: " + (int) ax + ":" + (int) ay);
		// for Top right
		bx = beeOrig.x * Math.cos(theta) - beeOrig.y * Math.sin(theta);
		by = beeOrig.x * Math.sin(theta) + beeOrig.y * Math.cos(theta);

		bee.set((float) bx, (float) by);
		logger.localDebugLog(tagSwitch, TAG, "bee - dx:dy: " + (int) bx + ":" + (int) by);
		logger.localDebugLog(1, TAG, "adx+bdx: " + (ax + bx) + "\nady+bdy: " + (ay + by));
	}

	public String toOrigString() {
		return "ayeOrig.x: " + ayeOrig.x + " ayeOrig.y: " + ayeOrig.y + " : beeOrig.x: " + beeOrig.x + " beeOrig.y: "
				+ beeOrig.y;
	}

	@Override
	public String toString() {
		return "aye.x: " + aye.x + " aye.y: " + aye.y + " : bee.x: " + bee.x + " bee.y: " + bee.y;
	}
}