package com.kinamod.catchme.util;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kinamod.catchme.CatchMe;
import com.kinamod.catchme.activities.HomeScreenActivity;

public class PreferenceHandler {
	private SharedPreferences prefs;
	CatchMe catchMe = CatchMe.getInstance();

	public void loadHighScores(Activity activity) {
		@SuppressWarnings("unused")
		final String TAG = "PrefStores";
		// getting preferences
		prefs = activity.getSharedPreferences(CatchMe.PREF_FILE_NAME, Context.MODE_PRIVATE);

		// get from the pref file, send to Game stae linked list

		LinkedList<Integer> scores = new LinkedList<Integer>();
		scores.add(prefs.getInt("HighScore", 0));
		scores.add(prefs.getInt("HighScore1", 0));
		scores.add(prefs.getInt("HighScore2", 0));
		scores.add(prefs.getInt("HighScore3", 0));
		scores.add(prefs.getInt("HighScore4", 0));
		scores.add(prefs.getInt("HighScore5", 0));
		scores.add(prefs.getInt("HighScore6", 0));
		scores.add(prefs.getInt("HighScore7", 0));
		scores.add(prefs.getInt("HighScore8", 0));
		scores.add(prefs.getInt("HighScore9", 0));
		catchMe.setHighScores(scores);
	}

	public synchronized void loadPrefsFromFile(HomeScreenActivity activity) {
		@SuppressWarnings("unused")
		final String TAG = "PrefStores";
		// getting preferences
		prefs = activity.getSharedPreferences(CatchMe.PREF_FILE_NAME, Context.MODE_PRIVATE);
		catchMe.setMusicON(prefs.getBoolean("Music", true));
		catchMe.setVibrate(prefs.getBoolean("Vibrate", true));
		catchMe.setInvertTilt(prefs.getBoolean("Invert", false));
		catchMe.setHighSensitivity(prefs.getBoolean("Sensitivity", true));

	}

	public void savePrefstoFile(HomeScreenActivity activity) {
		SharedPreferences prefs;
		Editor editor;
		// setting preferences
		prefs = activity.getSharedPreferences(CatchMe.PREF_FILE_NAME, Context.MODE_PRIVATE);
		editor = prefs.edit();
		editor.putBoolean("Vibrate", catchMe.isVibrate());
		editor.putBoolean("Music", catchMe.isMusicON());
		editor.putBoolean("SFX", catchMe.isSoundFX());
		editor.putBoolean("Sensitivity", catchMe.isHighSensitivity());
		editor.putBoolean("Invert", catchMe.isInvertTilt());
		editor.commit();
	}
}
