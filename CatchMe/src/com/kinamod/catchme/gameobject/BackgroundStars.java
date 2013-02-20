package com.kinamod.catchme.gameobject;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
	static int bmpHeight;
	static int bmpWidth;
	CatchMe catchMe = CatchMe.getInstance();
	static CustomisedLogging logger = new CustomisedLogging(false, false);

	Matrix mtrx = new Matrix(), slaveMtrx = new Matrix(), rotMatrix = new Matrix();
	static Bitmap myBitmap;
	float slavePosY = 0;
	private static boolean bitMapLoaded, withBGGame;

	public BackgroundStars(MainGameActivity mainGameActivity) {
		bmpHeight = catchMe.getScreenSize().y;
		bmpWidth = (int) (catchMe.getScreenSize().y * 1.3);
		if (myBitmap == null) {
			loadImage(mainGameActivity);
			slavePosY = -bmpHeight;
		}
	}

	public static void loadImage(final MainGameActivity mainGameActivity) {
		Thread bgPNGLoader = new Thread("bgPNGLoader") {
			public void run() {
				BitmapFactory.Options bmfOptions = new BitmapFactory.Options();
				bmfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
				try {
					withBGGame = true;
		myBitmap = Bitmap.createScaledBitmap(
BitmapFactory.decodeResource(mainGameActivity.getResources(),
							R.drawable.starrynightblack, bmfOptions),
							bmpWidth,
				bmpHeight, false);
		bitMapLoaded = true;
				} catch (OutOfMemoryError oOME) {
					System.gc();
					logger.localDebugLog(1, "OOME", "Ran out of memory, try again");
					oOME.printStackTrace();
					myBitmap = Bitmap.createScaledBitmap(
							BitmapFactory.decodeResource(mainGameActivity.getResources(), R.drawable.starrynightcopy),
							bmpWidth, bmpHeight, false);
					bitMapLoaded = true;
				}
			}
		};
			ScheduledThreadPoolExecutor bgPNGLoadRunner = new ScheduledThreadPoolExecutor(1);
			// explosionKiller.scheduleAtFixedRate(explosionThread, 0, 500,
			// TimeUnit.MILLISECONDS);
		int attempts = 0;
		while (!bitMapLoaded && (attempts < 2)) {
			attempts++;
			bgPNGLoadRunner.schedule(bgPNGLoader, 100, TimeUnit.MILLISECONDS);
		}
		if (!bitMapLoaded) {
			withBGGame = false;
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

	public static void releaseBitmap() {
		myBitmap.recycle();
		bitMapLoaded = false;
	}

	public void update(float dTime) {
		float basePosX;
		basePosY += dTime / 3;
		basePosX = -Math.abs(catchMe.getScreenSize().x / 1.2f
				* (float) Math.sin(Math.toRadians(catchMe.getRotateDegrees())));
		slavePosY = basePosY - bmpHeight;
		if (slavePosY >= 0) {
			basePosY = 0;
		}
		if (catchMe.isInvertTilt()) {
			rotMatrix.setRotate(-catchMe.getRotateDegrees(), catchMe.getScreenSize().x / 2,
					catchMe.getScreenSize().y / 2);
		} else {
			rotMatrix.setRotate(catchMe.getRotateDegrees(), catchMe.getScreenSize().x / 2,
					catchMe.getScreenSize().y / 2);
		}
		mtrx.setTranslate(basePosX, basePosY);

		// slaveMtrx.setRotate(GameState.getRotateDegrees(), basePosX,
		// slavePosY);
		slaveMtrx.setTranslate(basePosX, slavePosY);
	}

	public static boolean bitMapLoaded() {
		return bitMapLoaded;
	}

	public static boolean isBGGame() {
		return withBGGame;
	}
}
