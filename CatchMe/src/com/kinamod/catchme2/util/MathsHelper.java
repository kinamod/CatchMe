/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme2.util;

import java.util.Collection;

import android.graphics.Point;
import android.graphics.PointF;

public class MathsHelper {
	static CustomisedLogging logger = new CustomisedLogging(false, false);

	public static Point dividePoint(Point a, int b) {
		return new Point(a.x / b, a.y / b);
	}

	public static PointF dividePointF(PointF a, int b) {
		return new PointF(a.x / b, a.y / b);
	}

	public static float getHypF(PointF in) {
		// return (float) Math.sqrt(Math.pow(in.x, 2) + Math.pow(in.y, 2));
		return (float) Math.hypot(in.x, in.y);
	}

	public static <T extends Number> float meanCollection(Collection<T> collection) {
		float sum = 0;
		for (final T i : collection) {
			sum += i.floatValue();
		}
		return sum / collection.size();
	}

	/*
	 * @param A Point A of the line
	 * 
	 * @param B Point B of the line
	 * 
	 * @param P Point you need the distance from
	 * 
	 * @return shortest distance to that line
	 */
	public static float perpDistance(PointF A, PointF B, PointF P) {
		final double normalLength = Math.hypot(B.x - A.x, B.y - A.y);
		return (float) (Math.abs((P.x - A.x) * (B.y - A.y) - (P.y - A.y) * (B.x - A.x)) / normalLength);
	}

	public static Point subtractPoint(Point a, Point b) {
		return new Point(a.x - b.x, a.y - b.y);
	}

	public static PointF subtractPointF(PointF a, PointF b) {
		logger.localDebugLog(1, "MathsHelper.subtractPointF", a.x + " - " + b.x + " : " + a.y + " - " + b.y);
		return new PointF(a.x - b.x, a.y - b.y);
	}

	public static PointF subtractPointFP(PointF a, Point b) {
		return new PointF(a.x - b.x, a.y - b.y);
	}

	public static float xAndYtoDegrees(float orientX, float orientY) {
		return (float) Math.toDegrees(Math.atan(orientX / orientY));
	}

	private MathsHelper() {

	}
}
