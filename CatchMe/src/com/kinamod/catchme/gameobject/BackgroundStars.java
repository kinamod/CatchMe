package com.kinamod.catchme.gameobject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.kinamod.catchme.CatchMe;
import com.kinamod.catchme.R;
import com.kinamod.catchme.activities.MainGameActivity;
import com.kinamod.catchme.util.CustomisedLogging;

public class BackgroundStars {
	float basePosY = 0;
	int bmpHeight;
	int bmpWidth;
	CatchMe catchMe = CatchMe.getInstance();
	CustomisedLogging logger = new CustomisedLogging(false, false);

	Matrix mtrx = new Matrix(), slaveMtrx = new Matrix(),
			rotMatrix = new Matrix();
	Bitmap myBitmap;
	float slavePosY = 0;

	public BackgroundStars(MainGameActivity mainGameActivity) {
		bmpHeight = catchMe.getScreenSize().y;
		bmpWidth = (int) (catchMe.getScreenSize().y * 1.3);
		if (myBitmap == null) {
			myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					mainGameActivity.getResources(),
							R.drawable.starrynightcopy),
					bmpWidth, bmpHeight, false);
			slavePosY = -bmpHeight;
		}
	}

	public void drawBackgroundStars(Canvas canvas) {
		if (myBitmap == null) {
			return;
		}
		canvas.setMatrix(rotMatrix);
		canvas.drawBitmap(myBitmap, mtrx, null);
		canvas.drawBitmap(myBitmap, slaveMtrx, null);
		canvas.setMatrix(new Matrix());
	}

	public void releaseBitmap() {
		myBitmap.recycle();
	}

	public void update(float dTime) {
		float basePosX;
		basePosY += dTime / 3;
		basePosX = -Math.abs((catchMe.getScreenSize().x / 1.2f)
				* (float) Math.sin(Math.toRadians(catchMe.getRotateDegrees())));
		slavePosY = basePosY - bmpHeight;
		if (slavePosY >= 0) {
			basePosY = 0;
		}
		if (catchMe.isInvertTilt()) {
			rotMatrix.setRotate(-catchMe.getRotateDegrees(),
					catchMe.getScreenSize().x / 2,
					catchMe.getScreenSize().y / 2);
		} else {
			rotMatrix.setRotate(catchMe.getRotateDegrees(),
					catchMe.getScreenSize().x / 2,
					catchMe.getScreenSize().y / 2);
		}
		mtrx.setTranslate(basePosX, basePosY);

		// slaveMtrx.setRotate(GameState.getRotateDegrees(), basePosX,
		// slavePosY);
		slaveMtrx.setTranslate(basePosX, slavePosY);
	}
}
