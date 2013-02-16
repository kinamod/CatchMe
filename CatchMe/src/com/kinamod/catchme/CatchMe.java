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
import android.content.Context;
import android.graphics.Point;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;

import com.kinamod.catchme.activities.MainGameActivity;
import com.kinamod.catchme.util.CustomisedLogging;
import com.kinamod.catchme.util.MathsHelper;

public class CatchMe extends Application {
	public static final String AD_UNIT_ID = "a150f5362111431";
	public static final String PREF_FILE_NAME = "CatchMePrefsFile";
	private static CatchMe singleton;
	private static Vibrator vib;

	private static final CustomisedLogging logger = new CustomisedLogging(true, false);

	// private boolean aNewHighScore = false;
	// private int bucketSize;

	private int circleDelay;
	private boolean gameOver = false, paused = false;
	private LinkedList<Integer> highScores = new LinkedList<Integer>();
	int level = 1;
	private int multiplier = 1;

	private float rotateDegrees = 0;

	private int score = 0, levelScore = 0;

	private final Point screenSize = new Point();
	private Point centrePoint;

	private boolean vibrateON = true, musicON = true, highSensitivity = false, soundFX = true, invertTilt = false;
	private boolean scoreSubmitted = false, firstPlayed = false, gameStopped = true;
	public long time2;

	public static CatchMe getInstance() {
		if (singleton == null) {
			singleton = new CatchMe();
		}
		return singleton;
	}

	public void decPink(int take) {
		multiplier = 1;
		score -= take;
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

	public LinkedList<Integer> insertAndSortNewHighScore() {

		for (int i = 0; i < highScores.size(); i++) {
			if (score >= highScores.get(i)) {
				highScores.add(i, score);
				highScores.removeLast();
				highScores.addLast(i);
				// aNewHighScore = true;
				return highScores;
			}
		}
		// If there is no high score, the flag for which it is is removed
		highScores.removeLast();
		highScores.addLast(-1);
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
		logger.localDebugLog(1, "LoadPref", "music ON: " + musicON);
		return this.musicON;
	}

	// public boolean isNewHighScore() {
	// return aNewHighScore;
	// }

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

		// aNewHighScore = false;
		multiplier = 1;
		resetState();
	}

	private void resetState() {
		setPaused(false);
		setGameStopped(true);
		setGameOver(false);
	}

	public void setCircleDelay(int in) {
		this.circleDelay = in;
	}

	public void setGameOver(boolean gameOver) {
		logger.localDebugLog(1, "StateChnage", "setGameOver(" + gameOver + ")");
		this.gameOver = gameOver;
		setScoreSubmitted(false);
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
		logger.localDebugLog(1, "LoadPref", "    setMusic: " + isMusicON());
		this.musicON = musicON;
	}

	public void setRotateDegrees(float in) {
		if (invertTilt) {
			rotateDegrees = -in;
		} else {
			rotateDegrees = in;
		}
	}

	public void vibrate(int duration) {
		if (vib == null) {
			vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		}
		vib.vibrate(duration);
	}

	public void setScreenSize(MainGameActivity mainGameActivity) {
		final Display display = mainGameActivity.getWindowManager().getDefaultDisplay();
		final DisplayMetrics outMetrics = new DisplayMetrics();

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

	public boolean isScoreSubmitted() {
		return scoreSubmitted;
	}

	public void setScoreSubmitted(boolean scoreSubmitted) {
		this.scoreSubmitted = scoreSubmitted;
	}

	public boolean isFirstPlayed() {
		return firstPlayed;
	}

	public void firstPlayed() {
		this.firstPlayed = true;
	}

	public boolean isGameStopped() {
		return gameStopped;
	}

	public void setGameStopped(boolean gameStopped) {
		logger.localDebugLog(1, "StateChnage", "setGameStopped(" + gameStopped + ")");
		this.gameStopped = gameStopped;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		logger.localDebugLog(1, "StateChnage", "setPaused(" + paused + ")");
		this.paused = paused;
	}

}