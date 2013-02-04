/*
 * com.kinamod.catchMe.util.SoundPoolcatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.activities;

import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.kinamod.catchme.CatchMe;
import com.kinamod.catchme.R;
import com.kinamod.catchme.containers.Bucket;
import com.kinamod.catchme.containers.ExplosionContainer;
import com.kinamod.catchme.containers.FallingObjectContainer;
import com.kinamod.catchme.gameobject.BackgroundStars;
import com.kinamod.catchme.gameobject.FuelBar;
import com.kinamod.catchme.gameobject.HealthBar;
import com.kinamod.catchme.util.CustomisedLogging;
import com.kinamod.catchme.util.GameCanvas;
import com.kinamod.catchme.util.MathsHelper;
import com.kinamod.catchme.util.SoundPoolCatchMe;

public class MainGameActivity extends Activity {
	private static final CustomisedLogging logger = new CustomisedLogging(
			false, false);
	private static MainGameActivity MAIN_GAME_ACTIVITY;
	private static int noCircles = 0;

	public static MainGameActivity getInstance() {
		if (MAIN_GAME_ACTIVITY == null) {
			MAIN_GAME_ACTIVITY = new MainGameActivity();
		}
		return MAIN_GAME_ACTIVITY;
	}

	private BackgroundStars bgStars;
	public Bucket bucket;
	// private Thread execThread;
	private CatchMe catchMe = CatchMe.getInstance();
	private Thread circleThread, gameThread, explosionThread, musicThread,
			fuelRefillThread;
	private ScheduledExecutorService createCircle, gameExecutor, fRFill,
			explosionKiller;// executorStatus;
	private Editor editor;
	protected ExplosionContainer explosionContainer = new ExplosionContainer();
	protected FallingObjectContainer fOContainer;
	private FuelBar fuelBar;
	private GameCanvas gameCanvasView;
	private HealthBar healthBar;
	private long lastUpdate = 0;
	View.OnTouchListener mListenForSuperSquareMode = new View.OnTouchListener() {
		final String TAG = "onTouchListener - White";

		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			int action = motionEvent.getAction();
			logger.localDebugLog(2, TAG, "onTouch");
			if (action == MotionEvent.ACTION_DOWN) {
				if (fuelBar.getCount() > 0) {
					fuelBar.setInUse(true);
					playSound('z');
				}
			} else if (action == MotionEvent.ACTION_UP) {
				fuelBar.setInUse(false);
			}

			return true;
		}
	};
	private SensorManager mSensorManager;
	private MediaPlayer player;
	private SharedPreferences prefs;

	private SoundPoolCatchMe soundPool;

	private Vibrator vb;

	private void checkCollisions() {
		final String TAG = "checkCollisons";
		PointF circle = new PointF();
		PointF fromLineAye = new PointF();
		PointF fromLineBee = new PointF();
		int bucketHalfSize = bucket.getHalfBucketSize();
		float perpDistance;
		for (int bucketSide = 0; bucketSide < 4; bucketSide++) {
			logger.localDebugLog(1, TAG, "Check side: " + bucketSide);
			for (int whichCircle = 0; whichCircle < fOContainer.size(); whichCircle++) {
				logger.localDebugLog(1, TAG, "Check circle: " + whichCircle);
				boolean shouldICheck = fOContainer.shouldICheck(whichCircle,
						bucketHalfSize);
				if (!shouldICheck) {
					logger.localDebugLog(1, TAG, "Dont check");
					continue;
				}
				logger.localDebugLog(1, TAG, "Check");
				circle = MathsHelper.subtractPointFP(
						fOContainer.get(whichCircle).getPosition(),
						MathsHelper.dividePoint(catchMe.getScreenSize(), 2));
				fromLineAye = MathsHelper.subtractPointF(circle, bucket
						.getBucketLine(bucketSide).getAye());

				if (MathsHelper.getHypF(fromLineAye) < bucketHalfSize * 2) {
					// Possible Touch as within one side
					logger.localDebugLog(2, TAG, "Close to Aye");
					fromLineBee = MathsHelper.subtractPointF(circle, bucket
							.getBucketLine(bucketSide).getBee());

					if (MathsHelper.getHypF(fromLineBee) < bucketHalfSize * 2) {
						// Within Both sides, now check for circle Radius
						logger.localDebugLog(2, TAG, "Also close to Bee");
						perpDistance = MathsHelper.perpDistance(bucket
								.getBucketLine(bucketSide).getAye(), bucket
								.getBucketLine(bucketSide).getBee(), circle);

						if (perpDistance <= fOContainer.getCircleSize()) {
							fOContainer.get(whichCircle).setDying(true);
							logger.localDebugLog(2, TAG, "Touched the line");
							if (fOContainer.get(whichCircle).getColour() == Color.MAGENTA) {
								if (fuelBar.isInUse()) {// correct pink circle
									catchMe.incPink(5);

									playSound('b');
								} else {// incorrect pink circle
									catchMe.decPink(5);
									healthBar.decCount();
									explosionContainer.addExplosion(fOContainer
											.get(whichCircle).getBitmap(),
											fOContainer.get(whichCircle)
													.getPosition());
									if (catchMe.isVibrate()) {
										vb.vibrate(200);
									}
									playSound('g');
								}
							} else if (fOContainer.get(whichCircle).getColour() == bucket
									.getBucketLine(bucketSide).getColor()
									|| fuelBar.isInUse()) {
								logger.localDebugLog(2, TAG,
										"                    The colour line");
								catchMe.incScore();
								playSound('l'); // correct ANY circle
							} else {
								playSound('s'); // incorrect ANY circle
								catchMe.decPink(0);
								healthBar.decCount();
								explosionContainer.addExplosion(fOContainer
										.get(whichCircle).getBitmap(),
										fOContainer.get(whichCircle)
												.getPosition());
								checkHealth();
							}
						}
					}
				}
			}
		}
	}

	private void checkHealth() {
		if (healthBar.getCount() <= 0) {
			gameOver();
		}
	}

	private void endExecutors() {
		createCircle.shutdown();
		gameExecutor.shutdown();
		fRFill.shutdown();
	}

	// private void makeExecThread() {
	// final String TAG = "execThread";
	// execThread = new Thread("execThread") {
	//
	// public void run() {
	// logger.localDebugLog(2, TAG, "isShutDown: " + gameExecutor.isShutdown());
	// logger.localDebugLog(2, TAG, "isTerminated: " +
	// gameExecutor.isTerminated());
	// // gameCanvasView.postInvalidate();
	// }
	// };
	// }

	public void gameOver() {
		Intent intent = new Intent(getApplicationContext(),
				HomeScreenActivity.class);
		upDateHighScore();
		catchMe.setGameOver(true);
		startActivity(intent);

		finish();
	}

	private void generateCircle() {
		noCircles++;
		int clicksToCentre = 100;
		logger.localDebugLog(2, "noCircles", "Number of circles:- "
				+ fOContainer.size() + " of " + noCircles);

		fOContainer.makeNewFallingObject(clicksToCentre,
				randomStartPosition(catchMe.getScreenSize()),
				catchMe.getScreenSize());
	}

	public BackgroundStars getBgStars() {
		return bgStars;
	}

	public float getDeltaTimeMilli() {
		float last = lastUpdate;
		lastUpdate = System.nanoTime() / 1000000;
		return (lastUpdate - last);
	}

	public ExplosionContainer getExplosionContainer() {
		return explosionContainer;
	}

	public FallingObjectContainer getFOContainer() {
		return fOContainer;
	}

	public FuelBar getFuelBar() {
		return fuelBar;
	}

	public HealthBar getHealthBar() {
		return healthBar;
	}

	private void loadMusic() {
		if (catchMe.isMusicON()) {
			int random = (int) (Math.random() * 4);
			if (random == 0) {
				player = MediaPlayer.create(this, R.raw.catchmeorch);
			} else if (random == 1) {
				player = MediaPlayer.create(this, R.raw.easytune);
			} else if (random == 2) {
				player = MediaPlayer.create(this, R.raw.inyorkodd);
			} else if (random == 3) {
				player = MediaPlayer.create(this, R.raw.strangeones);
			}
		}
	}

	private void makeBars() {
		fuelBar = new FuelBar("White: ", 100);
		healthBar = new HealthBar("Health: ", 5);
	}

	private void makeCircleThread() {
		final String TAG = "runGame";
		circleThread = new Thread("circleThread") {
			@Override
			public void run() {
				generateCircle();
				logger.localDebugLog(2, TAG, "newCircle");

				// Check for gone circles
				fOContainer.killDeadCircles();

				logger.localDebugLog(2, "HowMany", "number of Circles"
						+ fOContainer.size());
			}

		};
	}

	private void makeExplosionThread() {
		@SuppressWarnings("unused")
		final String TAG = "runGame";
		explosionThread = new Thread("explosionThread") {
			@Override
			public void run() {
				// Check for finished explosions
				explosionContainer.killFinishedExplosions();
			}
		};
	}

	private void makeFuelRefillThread() {
		fuelRefillThread = new Thread("fuelRefillThread") {
			@Override
			public void run() {
				if (fuelBar.getCount() < 200) {
					fuelBar.incCount();
					fuelBar.incCount();
				}
			}
		};
	}

	private void makeGameSurfaceTransparent(SurfaceView sfvTrack) {
		sfvTrack.setZOrderOnTop(true); // necessary
		SurfaceHolder sfhTrack = sfvTrack.getHolder();
		sfhTrack.setFormat(PixelFormat.TRANSPARENT);
	}

	private void makeGameThread() {
		gameThread = new Thread("gameThread") {
			@Override
			public void run() {
				update();
			}
		};
		// gameThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		// {
		//
		// @Override
		// public void uncaughtException(Thread t, Throwable e) {
		// logger.localDebugLog(1, "Uncaught Exception", e.getMessage());
		// }
		// });
		// }
	}

	private void makeMusicThread(final int tryNo) {
		musicThread = new Thread("musicThread") {
			@Override
			public void run() {
				try {
					player.setLooping(true);
					player.start();
				} catch (NullPointerException ex) {
					if (tryNo < 10) {
						loadMusic();
						makeMusicThread(tryNo + 1);
					} else {
						ex.printStackTrace();
					}
				} catch (IllegalStateException ex) {
					logger.localDebugLog(1, "MediaPlayer",
							"IllegalStateException:\n" + ex.getCause());
				}
			}
		};
	}

	private void makeThreads() {
		if (catchMe.isMusicON()) {
			makeMusicThread(0);
		}
		makeCircleThread();
		makeFuelRefillThread();
		makeExplosionThread();
		makeGameThread();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		@SuppressWarnings("unused")
		final String TAG = "onCreate(Bundle savedInstanceState)";
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// setContentView(R.layout.game_screen_layout);
		// findViewById(R.id.actTwo_mainSurfaceView).setOnTouchListener(mListenForSuperSquareMode);

		catchMe.setCircleBucketFour(200, 100);
		catchMe.setScreenSize(this);

		setUpLocalPointers();
		// setupGameScreen

		// FrameLayout fLayout = (FrameLayout)
		// findViewById(R.id.actTwo_mainSurfaceView);
		// fLayout.addView(gameCanvasView);
		setContentView(gameCanvasView);
		makeGameSurfaceTransparent(gameCanvasView);
		gameCanvasView.setOnTouchListener(mListenForSuperSquareMode);
		// start sensors
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	@Override
	// override keycode_back if the menu is showing to do the same as the back
	// key on
	// screen
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			gameOver();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		final String TAG = "StopResume";
		logger.localDebugLog(2, TAG, "onPause");
		mSensorManager.unregisterListener(gameCanvasView);
		try {
			if (!(player == null)) {
				player.pause();
			}
		} catch (IllegalStateException e) {
			logger.localDebugLog(1, "MediaPlayer", "IllegalStateException:\n"
					+ e.getCause());
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// final String TAG = "StopResume";

		startGame();
		vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (Build.VERSION.SDK_INT <= 8) {
			mSensorManager.registerListener(gameCanvasView,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_GAME);
		} else {
			// Sensor.TYPE_GRAVITY( = 9) introduced in API 9(2.3 Gingerbread)
			mSensorManager.registerListener(gameCanvasView,
					mSensorManager.getDefaultSensor(0x00000009),
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	@Override
	protected void onStop() {
		final String TAG = "StopResume";
		logger.localDebugLog(2, TAG, "onStop");
		mSensorManager.unregisterListener(gameCanvasView);
		if (!(player == null)) {
			try {
				player.stop();
			} catch (IllegalStateException e) {
				logger.localDebugLog(1, "MediaPlayer",
						"IllegalStateException:\n" + e.getCause());
			}
			player.release();
		}
		endExecutors();
		bgStars.releaseBitmap();
		FallingObjectContainer.releaseBitMaps();
		super.onStop();
	}

	private void playSound(char sound) {
		if (!catchMe.isSoundFX()) {
			return;
		}
		switch (sound) {
		case 's':
			soundPool.playSound(2);
			break;
		case 'l':
			soundPool.playSound(1);
			break;
		case 'z':
			soundPool.playSound(4);
			break;
		// case 'w':
		// int whichOne = (int) (Math.random() * 3);
		// soundPool.playSound(6 + whichOne);
		// break;
		case 'd':
			soundPool.playSound(0);
			break;
		case 'g':
			soundPool.playSound(5);
			break;
		case 'b':
			soundPool.playSound(3);
			break;
		default:
			return;
		}
	}

	private Point randomStartPosition(Point size) {
		Point starting = new Point();
		int fromCentre = (size.y / 2);
		logger.localDebugLog(2, "Level", "Level: " + catchMe.getLevel());
		switch (catchMe.getLevel()) {
		case 1:
			// Always top
			starting.set(size.x / 2, 0);
			break;
		case 2:
			// top Or Bottom
			int random = (int) (Math.random() * 2);
			logger.localDebugLog(2, "Level", "Top?: " + random);
			starting.set(size.x / 2, size.y * random);
			break;
		case 3:
			// top or bottom left or right
			int random3 = (int) (Math.random() * 4);
			logger.localDebugLog(2, "Level", "Side?: " + random3);
			switch (random3) {
			case 0:
				// top
				logger.localDebugLog(2, "Level", "Top");
				starting.set(size.x / 2, 0);
				break;
			case 1:
				// bottom
				logger.localDebugLog(2, "Level", "Bottom");
				starting.set(size.x / 2, size.y);
				break;
			case 2:
				// left
				logger.localDebugLog(2, "Level", "Left");
				starting.set(0, size.y / 2);
				break;
			case 3:
				// right
				logger.localDebugLog(2, "Level", "Right");
				starting.set(size.x, size.y / 2);
				break;
			}
			break;
		default:
			int offSetX = size.x / 2;
			int offSetY = size.y / 2;
			int angle = (int) (Math.random() * 180);

			starting.x = (int) (fromCentre * Math.sin(Math.toRadians(angle)));

			starting.y = (int) (fromCentre * Math.cos(Math.toRadians(angle)));

			double whatQuadrant = Math.random();
			if (whatQuadrant >= 0.5) {
				starting.x = -starting.x;
			}
			whatQuadrant = Math.random();
			if (whatQuadrant >= 0.5) {
				starting.y = -starting.y;
			}
			starting.x += offSetX;
			starting.y += offSetY;
			break;
		}
		return starting;
	}

	private void resetGame() {
		catchMe.resetScore();
	}

	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// }

	private void setUpLocalPointers() {
		MAIN_GAME_ACTIVITY = getInstance();
		fOContainer = new FallingObjectContainer(this);
		bucket = new Bucket(this);
		soundPool = new SoundPoolCatchMe(this);
		bgStars = new BackgroundStars(this);

		gameCanvasView = new GameCanvas(this, this);

	}

	private void startGame() {

		resetGame();
		loadMusic();

		makeThreads();

		makeBars();

		gameExecutor = new ScheduledThreadPoolExecutor(1);
		gameExecutor.scheduleAtFixedRate(gameThread, 0, 1000 / 40,
				TimeUnit.MILLISECONDS);

		fRFill = new ScheduledThreadPoolExecutor(1);
		fRFill.scheduleAtFixedRate(fuelRefillThread, 0, 500,
				TimeUnit.MILLISECONDS);

		explosionKiller = new ScheduledThreadPoolExecutor(1);
		explosionKiller.scheduleAtFixedRate(explosionThread, 0, 500,
				TimeUnit.MILLISECONDS);

		createCircle = new ScheduledThreadPoolExecutor(1);
		// GameState.setCircleDelay(1600);

		createCircle.scheduleAtFixedRate(circleThread, 1000, 1600,
				TimeUnit.MILLISECONDS);

		// makeExecThread();
		// executorStatus = new ScheduledThreadPoolExecutor(1);
		// executorStatus.scheduleAtFixedRate(execThread, 0, 2,
		// TimeUnit.SECONDS);

		if (catchMe.isMusicON()) {
			musicThread.start();
		}
	}

	private void update() {
		float deltaMilli = getDeltaTimeMilli();
		// playSound('s');
		bucket.rotateBucket(catchMe.getRotateDegrees());
		// check any modes
		if (fuelBar.isInUse()) {
			bucket.whiteBucket();
		} else {
			bucket.normalBucket();
		}
		// update BG
		bgStars.update(deltaMilli);
		// update circle positions
		fOContainer.updateCirclePositions(deltaMilli);
		// update fuel Bars
		fuelBar.update(deltaMilli);
		healthBar.update(deltaMilli);
		checkCollisions();
		// gameCanvasView.postInvalidate();
	}

	private synchronized void upDateHighScore() {
		final String TAG = "PrefStores";
		// setting preferences
		prefs = this.getSharedPreferences(CatchMe.PREF_FILE_NAME,
				Context.MODE_PRIVATE);
		editor = prefs.edit();
		logger.localDebugLog(2, TAG,
				"PUT High Score: " + catchMe.getHighScore());

		LinkedList<Integer> scores = catchMe.getSortedHighScores();
		editor.putInt("HighScore", scores.get(0));
		editor.putInt("HighScore1", scores.get(1));
		editor.putInt("HighScore2", scores.get(2));
		editor.putInt("HighScore3", scores.get(3));
		editor.putInt("HighScore4", scores.get(4));
		editor.putInt("HighScore5", scores.get(5));
		editor.putInt("HighScore6", scores.get(6));
		editor.putInt("HighScore7", scores.get(7));
		editor.putInt("HighScore8", scores.get(8));
		editor.putInt("HighScore9", scores.get(9));
		editor.commit();
	}

}
