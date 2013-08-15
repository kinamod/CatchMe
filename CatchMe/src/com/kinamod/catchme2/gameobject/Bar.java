/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme2.gameobject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.kinamod.catchme2.CatchMe;
import com.kinamod.catchme2.util.CustomisedLogging;

public abstract class Bar {
	static final CustomisedLogging logger = new CustomisedLogging(false, false);
	CatchMe catchMe = CatchMe.getInstance();
	int count;
	boolean inUse = false;
	final String label;
	RectF myShape = new RectF();
	float decrementer, incrementer;

	public Bar(String label, int count) {
		this.label = label;
		decrementer = 1;
	}

	public void decCount() {
		if (count >= 0) {
			count -= decrementer;
		}
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
