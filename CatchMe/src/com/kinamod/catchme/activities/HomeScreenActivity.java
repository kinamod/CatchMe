/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.kinamod.catchme.CatchMe;
import com.kinamod.catchme.R;
import com.kinamod.catchme.util.CustomisedLogging;
import com.kinamod.catchme.util.PreferenceHandler;
import com.kinamod.catchme.util.ScoreHandler;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActivity;

public class HomeScreenActivity extends SwarmActivity {
	private AdView adView;
	private static HomeScreenActivity instance;
	private PreferenceHandler prefHand = new PreferenceHandler();
	CatchMe catchMe = CatchMe.getInstance();
	CustomisedLogging logger = new CustomisedLogging(true, false);
	boolean menuShowing = false;
	private static MediaPlayer player;
	private ImageView[] whiteButtons = new ImageView[9];

	private ScoreHandler scoreHand;

	ViewSwitcher vs;

	public void flipToHome(View view) {
		menuShowing = false;
		prefHand.savePrefstoFile(this);
		startMusic();
		vs.setAnimation(AnimationUtils.makeInAnimation(getApplicationContext(), true));
		vs.showPrevious();
	}

	// SWARM listeners ==============
	DialogInterface.OnClickListener swarmDashListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dia, int id) {
			Swarm.showDashboard();
		}
	};

	// SWARM listeners ============== END

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = getInstance();
		scoreHand = new ScoreHandler(this);
		// Make available outside google Play
		Swarm.enableAlternativeMarketCompatability();

		setContentView(R.layout.starting_screen);
		adView = new AdView(this, AdSize.BANNER, CatchMe.AD_UNIT_ID);

		vs = (ViewSwitcher) findViewById(R.id.flipView);

		LinearLayout adlayout = (LinearLayout) findViewById(R.id.adLinearLayout);

		// Add the adView to it
		adlayout.addView(adView);

		prefHand.loadPrefsFromFile(this);
		initialiseWhiteButtons();
		updateToggleButtons();
		prefHand.loadHighScores(this);
	}

	// Button Actions = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// = = = = = =

	public void showHighScores(View view) {
		animateButtQuick(view, 2);
		scoreHand.makeHighScoreDialog(getResources().getString(R.string.textHighScores), false);
	}

	public void flipToMenu(View view) {
		animateButtQuick(view, 3);

		menuShowing = true;
		vs.setAnimation(AnimationUtils.makeInAnimation(getApplicationContext(), false));
		vs.showNext();
	}

	public void howToPlay(View view) {
		animateButtQuick(view, 4);

		AlertDialog.Builder aDBuilder = new AlertDialog.Builder(this);
		aDBuilder.setTitle("How to Play").setMessage(R.string.texthowToPlay).create().show();
	}

	public void launchGame(View view) {
		catchMe.firstPlayed();
		Intent myIntent = new Intent(HomeScreenActivity.this, MainGameActivity.class);
		animateStartGame(view, myIntent);
	}

	// Toggle buttons = = = = =
	public void toggleSound(View v) {
		animateButtQuick(v, 5);

		ImageView img = (ImageView) findViewById(R.id.buttToggleSound);
		catchMe.setSoundFX(!catchMe.isSoundFX());

		if (catchMe.isSoundFX()) {
			img.setImageResource(R.drawable.button_sndon);
		} else {
			img.setImageResource(R.drawable.button_sndoff);
		}
	}

	public void toggleSens(View v) {
		animateButtQuick(v, 6);

		ImageView img = (ImageView) findViewById(R.id.buttToggleSens);
		catchMe.setHighSensitivity(!catchMe.isHighSensitivity());

		if (catchMe.isHighSensitivity()) {
			img.setImageResource(R.drawable.button_senshigh);
		} else {
			img.setImageResource(R.drawable.button_senslow);
		}
	}

	public void toggleInvert(View v) {
		animateButtQuick(v, 7);

		ImageView img = (ImageView) findViewById(R.id.buttToggleInvert);
		catchMe.setInvertTilt(!catchMe.isInvertTilt());

		if (catchMe.isInvertTilt()) {
			img.setImageResource(R.drawable.button_inverton);
		} else {
			img.setImageResource(R.drawable.button_invertoff);
		}
	}

	public void toggleMusic(View v) {
		animateButtQuick(v, 8);

		ImageView img = (ImageView) findViewById(R.id.buttToggleMusic);
		catchMe.setMusicON(!catchMe.isMusicON());

		if (catchMe.isMusicON()) {
			img.setImageResource(R.drawable.button_musicon);
		} else {
			img.setImageResource(R.drawable.button_musicoff);
		}
	}

	public void toggleVibrate(View v) {
		animateButtQuick(v, 9);

		ImageView img = (ImageView) findViewById(R.id.buttToggleVibrate);
		catchMe.setVibrate(!catchMe.isVibrate());

		if (catchMe.isVibrate()) {
			img.setImageResource(R.drawable.button_vibrateon);
		} else {
			img.setImageResource(R.drawable.button_vibrateoff);
		}
	}

	// Button Animators = = = =
	private void animateButtQuick(View v, final int which) {

		AlphaAnimation alphaUp = new AlphaAnimation(0f, 1.0f);
		alphaUp.setDuration(500);
		alphaUp.setFillAfter(true);
		alphaUp.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// setButtonOpacity(1, which - 1);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// setButtonOpacity(0, which - 1);
			}
		});
		if (v != null) {
			v.startAnimation(alphaUp);
		}
	}

	private void animateStartGame(View v, final Intent myIntent) {

		AnimationSet anSet = new AnimationSet(true);
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0f);
		alphaDown.setDuration(500);

		TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, -300,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		translate.setDuration(500);
		alphaDown.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				HomeScreenActivity.this.startActivity(myIntent);
				for (int i = 0; i < whiteButtons.length; i++) {
					setButtonOpacity(1, i);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				for (int i = 0; i < whiteButtons.length; i++) {
					setButtonOpacity(0, i);
				}
				TranslateAnimation goRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE,
						400, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				goRight.setDuration(500);
				findViewById(R.id.buttonHighScores).startAnimation(goRight);
				findViewById(R.id.buttonHowToPlay).startAnimation(goRight);
				findViewById(R.id.buttonOptions).startAnimation(goRight);
			}
		});
		anSet.addAnimation(alphaDown);
		anSet.addAnimation(translate);
		if (v != null) {
			v.startAnimation(anSet);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setButtonOpacity(int alpha, int which) {
		if (Build.VERSION.SDK_INT < 16) {
			whiteButtons[which].setAlpha(alpha);
		} else {
			whiteButtons[which].setImageAlpha(alpha);
		}
	}

	private void initialiseWhiteButtons() {
		whiteButtons[0] = (ImageView) findViewById(R.id.ImageView01);
		whiteButtons[1] = (ImageView) findViewById(R.id.ImageView02);
		whiteButtons[2] = (ImageView) findViewById(R.id.ImageView03);
		whiteButtons[3] = (ImageView) findViewById(R.id.ImageView04);
		whiteButtons[4] = (ImageView) findViewById(R.id.ImageView05);
		whiteButtons[5] = (ImageView) findViewById(R.id.ImageView06);
		whiteButtons[6] = (ImageView) findViewById(R.id.ImageView07);
		whiteButtons[7] = (ImageView) findViewById(R.id.ImageView08);
		whiteButtons[8] = (ImageView) findViewById(R.id.ImageView09);
	}

	// END Button Actions = = = = = = = = = = = = = = = = = = = = = = = = = = =
	// = = = = =

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
				logger.localDebugLog(1, "MediaPlayer", "IllegalStateException:\n" + e.getCause());
			}
			player.release();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		startMusic();
		// Initiate a generic request to load it with an ad
		setUpAd();
	}

	private void setUpAd() {
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		request.setGender(AdRequest.Gender.MALE);
		request.addTestDevice("D1AA72C9A2A1C5A11117338EA973D814");// my one s
		request.addTestDevice("BD7312D29024948D23A2FD559B4BFDD9");// chris'
																	// nexus
		adView.loadAd(request);
	}

	private void showGameOverDialog() {
		scoreHand.makeHighScoreDialog(getResources().getString(R.string.textGameOver), true);
		scoreHand.checkAcheivements();
	}

	private void startMusic() {
		if (player == null) {
			player = MediaPlayer.create(this, R.raw.tinynoisesintro);
		} else {
			player.release();
			player = MediaPlayer.create(this, R.raw.tinynoisesintro);
		}
		Thread t = new Thread("musicThread") {
			@Override
			public void run() {
				player.setLooping(true);
				if (catchMe.isMusicON()) {
					player.start();
				} else {
					player.pause();
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
		if (!catchMe.isMusicON()) {
			toggleMusic(null);
		}
		if (!catchMe.isHighSensitivity()) {
			toggleSens(null);
		}
		if (!catchMe.isSoundFX()) {
			toggleSound(null);
		}
		if (!catchMe.isVibrate()) {
			toggleVibrate(null);
		}
		if (!catchMe.isInvertTilt()) {
			toggleInvert(null);
		}
	}

	public static HomeScreenActivity getInstance() {
		if (instance == null) {
			instance = new HomeScreenActivity();
		}
		return instance;
	}
}