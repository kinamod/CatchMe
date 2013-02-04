/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.gameobject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.kinamod.catchme.util.CustomisedLogging;

public abstract class Bar {
	static final CustomisedLogging logger = new CustomisedLogging(false, false);
	int count;
	boolean inUse = false;
	final String label;
	RectF myShape = new RectF();

	public Bar(String label, int count) {
		this.label = label;
	}

	public void decCount() {
		count--;
	}

	public abstract void drawBar(Canvas canvas, Paint mPaint);

	// Getters and Setters

	public int getCount() {
		return count;
	}

	public void incCount() {
		count++;
	}

	public abstract void update(float dTime);
}
