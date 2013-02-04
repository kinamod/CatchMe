/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.containers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.SparseArray;

import com.kinamod.catchme.CatchMe;
import com.kinamod.catchme.R;
import com.kinamod.catchme.activities.MainGameActivity;
import com.kinamod.catchme.gameobject.BucketLine;
import com.kinamod.catchme.util.CustomisedLogging;

public class Bucket {
	static SparseArray<Bitmap> bucketBmps = new SparseArray<Bitmap>(6);
	static final CustomisedLogging logger = new CustomisedLogging(false, false);
	static SparseArray<Bitmap> whiteBucketBmps = new SparseArray<Bitmap>(5);

	public static void loadBucketBitmap(MainGameActivity mainGA) {
		bucketBmps.put(1, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.bucketnoglow1), 110, 110, false));
		bucketBmps.put(2, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.bucketnoglow2), 110, 110, false));
		bucketBmps.put(3, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.bucketnoglow3), 110, 110, false));
		bucketBmps.put(4, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.bucketnoglow4), 110, 110, false));
		bucketBmps.put(5, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.bucketnoglow5), 110, 110, false));
		bucketBmps.put(6, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.bucketnoglow6), 110, 110, false));

		whiteBucketBmps.put(1, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.whitebucket1), 110, 110, false));
		whiteBucketBmps.put(2, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.whitebucket2), 110, 110, false));
		whiteBucketBmps.put(3, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.whitebucket3), 110, 110, false));
		whiteBucketBmps.put(4, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.whitebucket4), 110, 110, false));
		whiteBucketBmps.put(5, Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(mainGA.getResources(),
						R.drawable.whitebucket5), 110, 110, false));
	}

	private int boxAnimate = 0, boxGraphic = 1;
	Bitmap bucketBitmap;
	BucketLine[] bucketLines;
	Matrix bucketMatrix = new Matrix();
	float[] bucketRotateMatrix = new float[16];
	CatchMe catchMe = CatchMe.getInstance();
	int[] col = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
	private float degrees;
	int halfBucketSize;
	float radius;
	private int wBoxAnimate = 0, wBoxGraphic = 1;

	private boolean whiteBucket;

	public Bucket(MainGameActivity activity) {
		PointF[] corners = new PointF[4];

		loadBucketBitmap(activity);
		halfBucketSize = 50; // xcbucketBitmap.getWidth() / 2; // size rather
		// than
		// height/width can be
		// used
		// because it's a square
		radius = (float) Math.sqrt(Math.pow(halfBucketSize * 2, 2) * 2);

		corners[0] = new PointF(-halfBucketSize, -halfBucketSize);
		corners[1] = new PointF(halfBucketSize, -halfBucketSize);
		corners[2] = new PointF(halfBucketSize, halfBucketSize);
		corners[3] = new PointF(-halfBucketSize, halfBucketSize);
		bucketLines = cornersToBucketLines(corners);
	}

	private float[] cornerInflate(PointF[] cornersIn) {
		float[] floats = new float[cornersIn.length * 4];
		floats[0] = cornersIn[0].x;
		floats[1] = cornersIn[0].y;
		int corner = 1;
		int limit = 4 * (cornersIn.length - 1);
		for (int floatIndex = 2; floatIndex < limit; floatIndex += 4) {
			floats[floatIndex] = (cornersIn[corner].x);
			floats[floatIndex + 1] = (cornersIn[corner].y);
			floats[floatIndex + 2] = (cornersIn[corner].x);
			floats[floatIndex + 3] = (cornersIn[corner].y);
			corner++;
		}
		floats[floats.length - 2] = (cornersIn[0].x);
		floats[floats.length - 1] = (cornersIn[0].y);
		return floats;
	}

	private BucketLine[] cornersToBucketLines(PointF[] cornersIn) {
		float[] floats = cornerInflate(cornersIn);

		BucketLine[] bL = new BucketLine[4];
		int multiplier = 0;
		for (int i = 0; i < bL.length; i++) {
			multiplier = 4 * i;
			bL[i] = new BucketLine(col[i], new PointF(floats[multiplier],
					floats[1 + multiplier]), new PointF(floats[2 + multiplier],
					floats[3 + multiplier]));
		}
		return bL;
	}

	private void drawBoundary(Canvas canvas, BucketLine[] bLines) {
		final String TAG = "drawBoundary";

		if (!whiteBucket) {
			if (boxAnimate > 2) {
				boxGraphic++;
				boxAnimate = 0;
			}
			if (boxGraphic > 6) {
				boxGraphic = 1;
			}
			boxAnimate++;
			logger.localDebugLog(1, TAG, "boxGraphicNo: " + boxGraphic);
			canvas.drawBitmap(bucketBmps.get(boxGraphic), bucketMatrix, null);
		} else {
			if (wBoxAnimate > 2) {
				wBoxGraphic++;
				wBoxAnimate = 0;
			}
			if (wBoxGraphic > 5) {
				wBoxGraphic = 1;
			}
			wBoxAnimate++;
			logger.localDebugLog(1, TAG, "whiteboxGraphicNo: " + boxGraphic);
			canvas.drawBitmap(whiteBucketBmps.get(wBoxGraphic), bucketMatrix,
					null);
		}
	}

	public void drawBucket(Canvas canvas, Point screenSize) {
		final String TAG = "drawBucket()";

		bucketMatrix.setTranslate((screenSize.x / 2) - 55,
				(screenSize.y / 2) - 55);
		bucketMatrix
				.postRotate(degrees, (screenSize.x / 2), (screenSize.y / 2));

		logger.localDebugLog(2, TAG, "matrix: " + bucketMatrix.toShortString());

		drawBoundary(canvas, bucketLines);
	}

	public BucketLine getBucketLine(int i) {
		return bucketLines[i];
	}

	// Getters and Setters====================================

	public Matrix getBucketMatrix() {
		return bucketMatrix;
	}

	public int getHalfBucketSize() {
		return halfBucketSize;
	}

	public void normalBucket() {
		whiteBucket = false;
	}

	public void rotateBucket(float degrees) {
		if (catchMe.isHighSensitivity()) {
			degrees = degrees * 2;
		}
		int offSet = 0; // 360 / bucketLines.length;
		this.degrees = degrees;
		for (int i = 0; i < bucketLines.length; i++) {
			bucketLines[i].rotateLine(radius, degrees + i * offSet);
		}
		// toString();
	}

	public void setBucketMatrix(Matrix bucketMatrix) {
		this.bucketMatrix = bucketMatrix;
	}

	@Override
	public String toString() {
		final String TAG = "toStringLines";
		StringBuilder sb = new StringBuilder();
		for (BucketLine bLines : bucketLines) {
			sb.append("line: " + bLines.toString() + "\n");
		}
		logger.localDebugLog(2, TAG, "go\n" + sb.toString());
		return sb.toString();
	}

	public void whiteBucket() {
		// Makes the bucket into superMode
		whiteBucket = true;
	}
}
