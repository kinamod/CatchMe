/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.gameobject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class HealthBar extends Bar {

	public HealthBar(String label, int countIn) {
		super(label, countIn);
		Point mSize = catchMe.getScreenSize();
		decrementer = (mSize.y - 30) / 5;
		myShape.set(mSize.x - 30, 20,
 mSize.x - 10, mSize.y - 20);
		incrementer = (mSize.y - 30) / (countIn / 2);

		this.count = mSize.y - 50;
	}

	@Override
	public void incCount() {
		if (count < catchMe.getScreenSize().y - 50) {
			count += incrementer;
		}
	}

	@Override
	public void drawBar(Canvas canvas, Paint mPaint) {
		if (!inUse) {
			mPaint.setColor(Color.rgb(200, 200, 200));
		} else {
			mPaint.setColor(Color.MAGENTA);
		}

		canvas.drawRoundRect(myShape, 10, 10, mPaint);
	}

	@Override
	public void update(float dTime) {
		myShape.top = (catchMe.getScreenSize().y - 10) - count;
	}
}