/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme2.gameobject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FuelBar extends Bar {

	public FuelBar(String label, int count) {
		super(label, count);
		this.count = count;
		decrementer = .5f;
		incrementer = (catchMe.getScreenSize().x - 160) / count;
		myShape.set(114, 60, 150 + incrementer * count, 85);
	}

	@Override
	public void drawBar(Canvas canvas, Paint mPaint) {
		if (!inUse) {
			mPaint.setColor(Color.rgb(200, 200, 200));
		} else {
			mPaint.setColor(Color.MAGENTA);
		}
		canvas.drawText(label, 0, 85, mPaint);
		canvas.drawRoundRect(myShape, 10, 10, mPaint);

	}

	public boolean isInUse() {
		return inUse;
	}

	@Override
	public void incCount() {
		if (count < catchMe.getScreenSize().x - 160) {
			count += 2;
		}
	}

	// Getters and Setters
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	@Override
	public void update(float dTime) {
		if (inUse && count > 0) {
			count -= dTime / 5;
			logger.localDebugLog(1, "updateFuel", "dTime: " + dTime);
		}
		myShape.right = 114 + incrementer * count;
		if (count <= 0) {
			myShape.right = 0;
			setInUse(false);
		}
	}
}
