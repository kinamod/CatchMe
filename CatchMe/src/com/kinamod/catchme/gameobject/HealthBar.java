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
	int barBottom;
	float rgColor, bColor;
	public HealthBar(String label, int countIn) {
		super(label, countIn);
		Point mSize = catchMe.getScreenSize();

		rgColor = 55f / 100f;
		bColor = 200f / 100f;

		barBottom = (mSize.y - 30);
		decrementer = (mSize.y - 90) / 5;
		myShape.set(mSize.x - 30, 100, mSize.x - 10, mSize.y - 30);
		incrementer = barBottom / (countIn / 2);

		this.count = mSize.y - 130;
	}

	@Override
	public void incCount() {
		if ((barBottom - count) > 100) {
			count += incrementer;
		}
	}

	@Override
	public void drawBar(Canvas canvas, Paint mPaint) {
		float red, green, blue;
		red = 200 + (55f * (1f - (count / ((float)catchMe.getScreenSize().y - 130))));
		green = 200f * (count / ((float) catchMe.getScreenSize().y - 130));
		blue = 200f * ((count / ((float) catchMe.getScreenSize().y - 130)));
		logger.localDebugLog(1, "TheColour", red + "," + green + "," + blue);
		mPaint.setColor(Color.rgb((int) red, (int) green, (int) blue));
		printColours(mPaint.getColor());
		canvas.drawRoundRect(myShape, 10, 10, mPaint);
	}

	private void printColours(int color) {

		int red, green, blue;

		red = color >> 0x10;

		green = color - (red << 16) >> 8;

		blue = color - (red << 16) - (green << 8);

		StringBuilder sb = new StringBuilder();
		sb.append("red: " + red);
		sb.append(" - green: " + green);
		sb.append(" - blue: " + blue);
		logger.localDebugLog(2, "TheColour", sb.toString());

	}

	@Override
	public void update(float dTime) {
		myShape.top = barBottom - count;
	}
}