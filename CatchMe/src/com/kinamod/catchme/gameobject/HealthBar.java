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

import com.kinamod.catchme.CatchMe;

public class HealthBar extends Bar {
	CatchMe catchMe = CatchMe.getInstance();
	int multiplier = 180;

	public HealthBar(String label, int count) {
		super(label, count);
		Point mSize = catchMe.getScreenSize();
		multiplier = mSize.y / (count + 1);
		this.count = count;
		myShape.set(mSize.x - 30, (mSize.y - 10) - (count * multiplier),
				mSize.x - 10, mSize.y - 10);

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
		myShape.top = (catchMe.getScreenSize().y - 10) - (count * multiplier);
	}
}