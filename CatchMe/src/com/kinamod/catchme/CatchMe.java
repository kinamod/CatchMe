/*
 * com.kinamod.catchme.CatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme;

import java.util.Collection;
import java.util.LinkedList;

import android.app.Application;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

import com.kinamod.catchme.activities.MainGameActivity;
import com.kinamod.catchme.util.MathsHelper;

public class CatchMe extends Application {
	public static final String AD_UNIT_ID = "a150f5362111431";
	public static final String PREF_FILE_NAME = "CatchMePrefsFile";

	private static CatchMe singleton;
//HI
	public static CatchMe getInstance() {
		if (singleton == null) {
			
			singleton = new CatchMe();
		}
		return singleton;
	}

	private boolean aNewHighScore = false;
	private int bucketSize;

	private int circleDelay;
	private boolean gameOver = false;
	private LinkedList<Integer> highScores = new LinkedList<Integer>();
	int level = 1;
	private int multiplier = 1;

	private float rotateDegrees = 0;

	private int score = 0, levelScore = 0;

	private Point screenSize = new Point(), centrePoint;

	private boolean vibrateON = true, musicON = true, highSensitivity = false,
			soundFX = true, invertTilt = false;

	public void decPink(int take) {
		multiplier = 1;
		score -= take;
	}

	public int getBucketSize() {
		return bucketSize;
	}

	public Point getCentrePoint() {
		return centrePoint;
	}

	// Getters and Setters
	public int getCircleDelay() {
		return circleDelay;
	}

	public int getHighScore() {
		return highScores.get(0);
	}

	public LinkedList<Integer> getHighScores() {
		return highScores;
	}

	public int getLevel() {
		return level;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public float getRotateDegrees() {
		return rotateDegrees;
	}

	public int getScore() {
		return score;
	}

	public Point getScreenSize() {
		return screenSize;
	}

	public LinkedList<Integer> getSortedHighScores() {
		for (int i = 0; i < highScores.size(); i++) {
			if (score >= highScores.get(i)) {
				highScores.add(i, score);
				highScores.removeLast();
				highScores.addLast(i);
				aNewHighScore = true;
				return highScores;
			}
		}
		return highScores;
	}

	public void incPink(int add) {
		for (int i = 0; i < add; i++) {
			incScore();
		}
		multiplier++;
	}

	public void incScore() {
		score += 1 * multiplier * level;
		if (score > levelScore) {
			levelScore++;
			if (levelScore % 10 == 0) {
				nextLevel();
			}
		}
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public boolean isHighSensitivity() {
		return highSensitivity;
	}

	public boolean isInvertTilt() {
		return invertTilt;
	}

	public boolean isMusicON() {
		return musicON;
	}

	public boolean isNewHighScore() {
		return aNewHighScore;
	}

	public boolean isSoundFX() {
		return soundFX;
	}

	public boolean isVibrate() {
		return vibrateON;
	}

	private void nextLevel() {
		level++;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public void resetScore() {
		score = 0;
		levelScore = 0;
		level = 1;
		gameOver = false;
		aNewHighScore = false;
		multiplier = 1;
	}

	public void setCircleBucketFour(int circleDelay, int bucketSize) {
		this.bucketSize = bucketSize;
	}

	public void setCircleDelay(int in) {
		this.circleDelay = in;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public void setHighScores(Collection<Integer> scores) {
		highScores = (LinkedList<Integer>) scores;
	}

	public void setHighSensitivity(boolean highSensitivity) {
		this.highSensitivity = highSensitivity;
	}

	public void setInvertTilt(boolean in) {
		invertTilt = in;
	}

	public void setMusicON(boolean musicON) {
		this.musicON = musicON;
	}

	public void setRotateDegrees(float in) {
		if (invertTilt) {
			rotateDegrees = -in;
		} else {
			rotateDegrees = in;
			// hi
		}
	}

	public void setScreenSize(MainGameActivity mainGameActivity) {
		Display display = mainGameActivity.getWindowManager()
				.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();

		display.getMetrics(outMetrics);
		screenSize.x = outMetrics.widthPixels;
		screenSize.y = outMetrics.heightPixels;
		centrePoint = MathsHelper.dividePoint(screenSize, 2);
	}

	public void setSoundFX(boolean soundFX) {
		this.soundFX = soundFX;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrateON = vibrate;
	}

}