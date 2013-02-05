/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.activities;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.kinamod.catchme.CatchMe;
import com.kinamod.catchme.R;
import com.kinamod.catchme.util.CustomisedLogging;

public class HomeScreenActivity extends Activity {
	private AdView adView;
	ToggleButton buttMusic, buttSensitivity, buttFX, buttVibrate,
			buttInvertTilt;
	CatchMe catchMe = CatchMe.getInstance();
	CustomisedLogging logger = new CustomisedLogging(true, false);
	boolean menuShowing = false;
	MediaPlayer player;
	private SharedPreferences prefs;
	ViewSwitcher vs;

	public void flipToHome(View view) {
		menuShowing = false;
		pushPreferencestoGameState();
		pushMusicandVibratetoPrefs();
		vs.setAnimation(AnimationUtils.makeInAnimation(getApplicationContext(),
				true));
		vs.showPrevious();
	}

	public void flipToMenu(View view) {
		menuShowing = true;
		vs.setAnimation(AnimationUtils.makeInAnimation(getApplicationContext(),
				false));
		vs.showNext();
	}

	private void loadHighScoesFromPrefs() {
		@SuppressWarnings("unused")
		final String TAG = "PrefStores";
		// getting preferences
		prefs = this.getSharedPreferences(CatchMe.PREF_FILE_NAME,
				Context.MODE_PRIVATE);

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

	public void howToPlay(View view) {
		AlertDialog.Builder aDBuilder = new AlertDialog.Builder(this);
		aDBuilder.setTitle("How to Play").setMessage(R.string.texthowToPlay)
				.create().show();
	}

	public void launchGame(View view) {
		Intent myIntent = new Intent(HomeScreenActivity.this,
				MainGameActivity.class);
		// myIntent.putExtra("fourSides", true);
		HomeScreenActivity.this.startActivity(myIntent);
	}

	private synchronized void loadInPrefsAndHighScore() {
		@SuppressWarnings("unused")
		final String TAG = "PrefStores";
		// getting preferences
		prefs = this.getSharedPreferences(CatchMe.PREF_FILE_NAME,
				Context.MODE_PRIVATE);
		catchMe.setMusicON(prefs.getBoolean("Music", true), player);
		catchMe.setVibrate(prefs.getBoolean("Vibrate", true));
		catchMe.setInvertTilt(prefs.getBoolean("Invert", false));
		catchMe.setHighSensitivity(prefs.getBoolean("Sensitivity", true));
		updateToggleButtons();
		pushPreferencestoGameState();
		loadHighScoesFromPrefs();
	}

	private void makeHighScoreDialog(String inString, boolean gameOvr) {
		LinkedList<Integer> scores = catchMe.getHighScores();
		StringBuilder sb = new StringBuilder();
		boolean i = true;
		logger.localDebugLog(1, "makeHighScoreDialog",
				"size(): " + scores.size());

		if (0 == scores.getLast() && i) {
			sb.append(">> 1st  " + scores.get(0) + " <<\n");
			i = false;
		} else {
			sb.append("1st  " + scores.get(0) + "\n");
		}

		if (1 == scores.getLast() && i) {
			sb.append(">> 2nd  " + scores.get(1) + " <<\n");
			i = false;
		} else {
			sb.append("2nd  " + scores.get(1) + "\n");
		}

		if (2 == scores.getLast() && i) {
			sb.append(">> 3rd  " + scores.get(2) + " <<\n");
			i = false;
		} else {
			sb.append("3rd  " + scores.get(2) + "\n");
		}

		if (3 == scores.getLast() && i) {
			sb.append(">> 4th  " + scores.get(3) + " <<\n");
			i = false;
		} else {
			sb.append("4th  " + scores.get(3) + "\n");
		}

		if (4 == scores.getLast() && i) {
			sb.append(">> 5th  " + scores.get(4) + " <<\n");
			i = false;
		} else {
			sb.append("5th  " + scores.get(4) + "\n");
		}

		if (5 == scores.getLast() && i) {
			sb.append(">> 6th  " + scores.get(5) + " <<\n");
			i = false;
		} else {
			sb.append("6th  " + scores.get(5) + "\n");
		}

		if (6 == scores.getLast() && i) {
			sb.append(">> 7th  " + scores.get(6) + " <<\n");
			i = false;
		} else {
			sb.append("7th  " + scores.get(6) + "\n");
		}

		if (7 == scores.getLast() && i) {
			sb.append(">> 8th  " + scores.get(7) + " <<\n");
			i = false;
		} else {
			sb.append("8th  " + scores.get(7) + "\n");
		}

		if (8 == scores.getLast() && i) {
			sb.append(">> 9th  " + scores.get(8) + " <<\n");
			i = false;
		} else {
			sb.append("9th  " + scores.get(8) + "\n");
		}

		if (9 == scores.getLast() && i) {
			sb.append(">> 10th  " + scores.get(9) + " <<\n");
			i = false;
		} else {
			sb.append("10th  " + scores.get(9) + "\n");
		}

		if (gameOvr) {
			sb.append("\n\n" + getResources().getString(R.string.textYourScore)
					+ "  " + catchMe.getScore());
		}
		AlertDialog.Builder aDBuilder = new AlertDialog.Builder(this);
		if (gameOvr) {
			aDBuilder.setTitle(inString).setMessage(sb.toString()).create()
					.show();
		} else {
			aDBuilder.setTitle(inString).setMessage(sb.toString()).create()
					.show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.starting_screen);
		adView = new AdView(this, AdSize.BANNER, CatchMe.AD_UNIT_ID);

		vs = (ViewSwitcher) findViewById(R.id.flipView);

		LinearLayout adlayout = (LinearLayout) findViewById(R.id.adLinearLayout);

		// Add the adView to it
		adlayout.addView(adView);
		initToggleButtons();
		loadInPrefsAndHighScore();
	}

	// Button Actions = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// = = = = = =

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}

		unbindDrawables(findViewById(R.id.homeScreenFull));
		System.gc();
		super.onDestroy();
	}

	@Override
	// override keycode_back if the menu is showing to do the same as the back
	// key on
	// screen
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (menuShowing) {
				flipToHome(null);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		setIntent(newIntent);
		if (catchMe.isGameOver()) {
			catchMe.setGameOver(false);
			showGameOverDialog();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!(player == null)) {
			try {
				player.stop();
			} catch (IllegalStateException e) {
				logger.localDebugLog(1, "MediaPlayer",
						"IllegalStateException:\n" + e.getCause());
			}
			player.release();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		player = MediaPlayer.create(this, R.raw.tinynoisesintro);
		startMusic();
		// Initiate a generic request to load it with an ad
		setUpAd();
	}

	private void pushMusicandVibratetoPrefs() {
		SharedPreferences prefs;
		Editor editor;
		// setting preferences
		prefs = this.getSharedPreferences(CatchMe.PREF_FILE_NAME,
				Context.MODE_PRIVATE);
		editor = prefs.edit();
		editor.putBoolean("Vibrate", catchMe.isVibrate());
		editor.putBoolean("Music", catchMe.isMusicON());
		editor.putBoolean("SFX", catchMe.isSoundFX());
		editor.putBoolean("Sensitivity", catchMe.isHighSensitivity());
		editor.putBoolean("Invert", catchMe.isInvertTilt());
		editor.commit();

	}

	// END Button Actions = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// = = = = =

	private void pushPreferencestoGameState() {
		catchMe.setMusicON(buttMusic.getText().equals("ON"), player);
		catchMe.setSoundFX(buttFX.getText().equals("ON"));
		catchMe.setHighSensitivity(buttSensitivity.getText().equals("High"));
		catchMe.setVibrate(buttVibrate.getText().equals("ON"));
		catchMe.setInvertTilt(buttInvertTilt.getText().equals("ON"));

		// String string = "" + GameState.isMusicON() + GameState.isSoundFX() +
		// GameState.isHighSensitivity()
		// + GameState.isVibrate();
		// Toast.makeText(getApplicationContext(), string,
		// Toast.LENGTH_LONG).show();
	}

	private void setUpAd() {
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		request.setGender(AdRequest.Gender.MALE);
		request.addTestDevice("D1AA72C9A2A1C5A11117338EA973D814");
		adView.loadAd(request);

	}

	private void initToggleButtons() {
		buttSensitivity = (ToggleButton) findViewById(R.id.toggleSensitivity);
		buttMusic = (ToggleButton) findViewById(R.id.toggleMusic);
		buttFX = (ToggleButton) findViewById(R.id.toggleSound);
		buttVibrate = (ToggleButton) findViewById(R.id.toggleVibrate);
		buttInvertTilt = (ToggleButton) findViewById(R.id.buttInvertTilt);

	}

	private void showGameOverDialog() {
		makeHighScoreDialog(getResources().getString(R.string.textGameOver),
				true);
	}

	public void showHighScores(View view) {
		makeHighScoreDialog(getResources().getString(R.string.textHighScores),
				false);
	}

	private void startMusic() {
		Thread t = new Thread("musicThread") {
			@Override
			public void run() {
				player.setLooping(true);
				if (catchMe.isMusicON()) {
					player.start();
				}
			}
		};
		t.start();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	private void updateToggleButtons() {
		buttMusic.setChecked(catchMe.isMusicON());
		buttSensitivity.setChecked(catchMe.isHighSensitivity());
		buttFX.setChecked(catchMe.isSoundFX());
		buttVibrate.setChecked(catchMe.isVibrate());
		buttInvertTilt.setChecked(catchMe.isInvertTilt());
	}
}