/*
 * com.kinamod.catchMe.util.SoundPoolcatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.activities;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import com.swarmconnect.SwarmActivity;

public class MainGameActivity extends SwarmActivity {
	private static final CustomisedLogging logger = new CustomisedLogging(false, false);
	private static MainGameActivity MAIN_GAME_ACTIVITY;
	private static int noCircles = 0;
	SurfaceHolder surfaceHolder;

	public static MainGameActivity getInstance() {
		// if (MAIN_GAME_ACTIVITY == null) {
		// MAIN_GAME_ACTIVITY = new MainGameActivity();
		// }
		return MAIN_GAME_ACTIVITY;
	}

	private BackgroundStars bgStars;
	public Bucket bucket;
	// private Thread execThread;
	private final CatchMe catchMe = CatchMe.getInstance();
	private Thread circleThread, gameThread, explosionThread, fuelRefillThread;
	private ScheduledExecutorService createCircle, gameExecutor, fRFill, explosionKiller;// executorStatus;
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
			final int action = motionEvent.getAction();
			logger.localDebugLog(2, TAG, "onTouch");
			if (action == MotionEvent.ACTION_DOWN) {
				if (catchMe.isPaused()) {
					// unPause();
					onResume();
					return true;
				}
				if (!catchMe.isGameStopped()) {
					if (fuelBar.getCount() > 0) {
						fuelBar.setInUse(true);
						playSound('z');
					}
				} else {
					triggerGameAnimation(0);
					catchMe.setGameStopped(false);
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
	private boolean restarting;

	private void checkCollisions() {
		final String TAG = "checkCollisons";
		PointF circle = new PointF();
		PointF fromLineAye = new PointF();
		PointF fromLineBee = new PointF();
		final int bucketHalfSize = bucket.getHalfBucketSize();
		float perpDistance;
		for (int bucketSide = 0; bucketSide < 4; bucketSide++) {
			logger.localDebugLog(2, TAG, "Check side: " + bucketSide);
			for (int whichCircle = 0; whichCircle < fOContainer.size(); whichCircle++) {
				logger.localDebugLog(2, TAG, "Check circle: " + whichCircle);
				final boolean shouldICheck = fOContainer.shouldICheck(whichCircle, bucketHalfSize);
				if (!shouldICheck) {
					logger.localDebugLog(2, TAG, "Dont check");
					continue;
				}
				logger.localDebugLog(2, TAG, "Check");
				circle = MathsHelper.subtractPointFP(fOContainer.get(whichCircle).getPosition(),
						MathsHelper.dividePoint(catchMe.getScreenSize(), 2));
				fromLineAye = MathsHelper.subtractPointF(circle, bucket.getBucketLine(bucketSide).getAye());

				if (MathsHelper.getHypF(fromLineAye) < bucketHalfSize * 2) {
					// Possible Touch as within one side
					logger.localDebugLog(2, TAG, "Close to Aye");
					fromLineBee = MathsHelper.subtractPointF(circle, bucket.getBucketLine(bucketSide).getBee());

					if (MathsHelper.getHypF(fromLineBee) < bucketHalfSize * 2) {
						// Within Both sides, now check for circle Radius
						logger.localDebugLog(2, TAG, "Also close to Bee");
						perpDistance = MathsHelper.perpDistance(bucket.getBucketLine(bucketSide).getAye(), bucket
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
									explosionContainer.addExplosion(fOContainer.get(whichCircle).getBitmap(),
											fOContainer.get(whichCircle).getPosition());
									if (catchMe.isVibrate()) {
										catchMe.vibrate(200);
									}
									playSound('g');
								}
							} else if (fOContainer.get(whichCircle).getColour() == bucket.getBucketLine(bucketSide)
									.getColor() || fuelBar.isInUse()) {
								logger.localDebugLog(2, TAG, "                    The colour line");
								catchMe.incScore();
								playSound('l'); // correct ANY circle
							} else {
								playSound('s'); // incorrect ANY circle
								catchMe.decPink(0);
								healthBar.decCount();
								explosionContainer.addExplosion(fOContainer.get(whichCircle).getBitmap(), fOContainer
										.get(whichCircle).getPosition());
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
		gameExecutor.shutdown();
		explosionKiller.shutdown();
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

	private void generateCircle() {
		noCircles++;
		final int clicksToCentre = 100;
		logger.localDebugLog(2, "noCircles", "Number of circles:- " + fOContainer.size() + " of " + noCircles);

		fOContainer.makeNewFallingObject(clicksToCentre, randomStartPosition(catchMe.getScreenSize()),
				catchMe.getScreenSize());
	}

	public BackgroundStars getBgStars() {
		return bgStars;
	}

	public float getDeltaTimeMilli() {
		final float last = lastUpdate;
		lastUpdate = System.nanoTime() / 1000000;
		return lastUpdate - last;
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
		final int random = (int) (Math.random() * 4);
		if (random == 0) {
			player = MediaPlayer.create(MAIN_GAME_ACTIVITY, R.raw.catchmeorch);
		} else if (random == 1) {
			player = MediaPlayer.create(MAIN_GAME_ACTIVITY, R.raw.easytune);
		} else if (random == 2) {
			player = MediaPlayer.create(MAIN_GAME_ACTIVITY, R.raw.inyorkodd);
		} else if (random == 3) {
			player = MediaPlayer.create(MAIN_GAME_ACTIVITY, R.raw.strangeones);
		}
	}

	private void makeBars() {
		fuelBar = new FuelBar("White: ", 100);
		healthBar = new HealthBar("Health: ", 200);
	}

	private void makeCircleThread() {
		final String TAG = "runGame";
		circleThread = new Thread("circleThread") {
			int delay = 1600;

			@Override
			public void run() {
				generateCircle();
				logger.localDebugLog(2, TAG, "newCircle");

				if (delay > 1050) {
					delay -= 10;
					logger.localDebugLog(2, "FallingFreq", "Next Fall: " + delay);
				}

				createCircle.schedule(this, delay, TimeUnit.MILLISECONDS);
				logger.localDebugLog(2, "HowMany", "number of Circles" + fOContainer.size());
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
					healthBar.incCount();
				}
			}
		};
	}

	private void makeGameSurfaceTransparent(SurfaceView sfvTrack) {
		sfvTrack.setZOrderOnTop(true); // necessary
		surfaceHolder = sfvTrack.getHolder();
		surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
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
		// logger.localDebugLog(2, "Uncaught Exception", e.getMessage());
		// }
		// });
		// }
	}

	private void playMusic() {

		if (catchMe.isMusicON()) {
			loadMusic();
			new Thread("musicThread") {
				@Override
				public void run() {
					try {
						player.setLooping(true);
						player.start();
					}
					// catch (final NullPointerException ex) {
					// if (tryNo < 10) {
					// loadMusic();
					// makeMusicThread(tryNo + 1);
					// } else {
					// ex.printStackTrace();
					// }
					// }
					catch (final IllegalStateException ex) {
						logger.localDebugLog(2, "MediaPlayer", "IllegalStateException:\n" + ex.getCause());
					}
				}
			}.start();
		}
	}

	private void makeThreads() {

			new Thread("loadingListeners") {
				public void run() {
					playMusic();
				}

			}.start();
		makeCircleThread();
		makeFuelRefillThread();
		makeExplosionThread();
		makeGameThread();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		long time = System.currentTimeMillis();
		MAIN_GAME_ACTIVITY = this;
		logger.localDebugLog(1, "TIMER", "MGA Create Started: " + time);
		@SuppressWarnings("unused")
		final String TAG = "onCreate(Bundle savedInstanceState)";
		super.onCreate(savedInstanceState);
		// Remove title bar
		MAIN_GAME_ACTIVITY.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		MAIN_GAME_ACTIVITY.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// setContentView(R.layout.game_screen_layout);
		// findViewById(R.id.actTwo_mainSurfaceView).setOnTouchListener(mListenForSuperSquareMode);
		time = System.currentTimeMillis();

		catchMe.setScreenSize(MAIN_GAME_ACTIVITY);
		logger.localDebugLog(1, "TIMER", "setBucket and setScreen: " + (System.currentTimeMillis() - time));

		time = System.currentTimeMillis(); // TODO DSJ
		logger.localDebugLog(1, "TIMER", "    - - - - ALL Local Pointers:");
		setUpLocalPointers();
		// setupGameScreen
		logger.localDebugLog(1, "TIMER", "    - - - - ALL Local Pointers: " + (System.currentTimeMillis() - time)); // TODO
		time = System.currentTimeMillis(); // TODO DSJ // DSJ
		// FrameLayout fLayout = (FrameLayout)
		// findViewById(R.id.actTwo_mainSurfaceView);
		// fLayout.addView(gameCanvasView);
		setContentView(gameCanvasView);
		logger.localDebugLog(1, "TIMER", "Done Set Content View: " + (System.currentTimeMillis() - time));
		makeGameSurfaceTransparent(gameCanvasView);
		gameCanvasView.setOnTouchListener(mListenForSuperSquareMode);
		time = System.currentTimeMillis(); // TODO DSJ
		// start sensors
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		logger.localDebugLog(1, "TIMER", "MGA Create Finished: " + (System.currentTimeMillis() - time));
	}

	@Override
	// override keycode_back if the menu is showing to do the same as the back
	// key on
	// screen
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (catchMe.isPaused()) {
				gameOver();
			} else if (!catchMe.isGameStopped()) {
				onPause();
			} else {
				gameOver();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// private void pause() {
	// player.pause();
	// catchMe.setPaused(true);
	// if (createCircle != null) {
	// createCircle.shutdown();
	// }
	// if (fRFill != null) {
	// fRFill.shutdown();
	// }
	// }
	//
	// private void unPause() {
	// catchMe.setPaused(false);
	// player.start();
	// triggerGameAnimation(1000);
	// }

	public void gameOver() {

		logger.localDebugLog(1, "StateChnage", "gameOver");
		endExecutors();
		// pause();
		final Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
		upDateHighScore();

		catchMe.setPaused(false);
		catchMe.setGameOver(true);
		catchMe.setGameStopped(true);

		startActivity(intent);

		finish();
	}

	@Override
	protected void onPause() {
		logger.localDebugLog(1, "StateChnage", "onPause");
		super.onPause();
		final String TAG = "StopResume";
		if (restarting) {
			restarting = false;
		}
		if (createCircle != null) {
			createCircle.shutdown();
		}
		if (fRFill != null) {
			fRFill.shutdown();
		}
		logger.localDebugLog(2, TAG, "onPause");
		// endExecutors();
		// mSensorManager.unregisterListener(gameCanvasView);
		// try {
		// if (!(player == null)) {
		// player.pause();
		// }
		// } catch (IllegalStateException e) {
		// logger.localDebugLog(2, "MediaPlayer", "IllegalStateException:\n" +
		// e.getCause());
		// }
		catchMe.setPaused(true);
	}

	@Override
	public void onStart() {

		logger.localDebugLog(1, "StateChnage", "onStart");
		super.onStart();
		if (catchMe.isGameOver() || catchMe.isGameStopped()) {
			long time = System.currentTimeMillis(); // TODO DSJ
			startGame();
			logger.localDebugLog(1, "TIMER", "startGame-FULL: " + (System.currentTimeMillis() - time));

		} else if (catchMe.isPaused()) {
			restarting = true;
		}
		long time = System.currentTimeMillis(); // TODO DSJ
		new Thread("loadingListeners") {
			public void run() {
				if (Build.VERSION.SDK_INT <= 8) {
					mSensorManager
							.registerListener(gameCanvasView,
									mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
									SensorManager.SENSOR_DELAY_GAME);
				} else {
					// Sensor.TYPE_GRAVITY( = 9) introduced in API 9(2.3
					// Gingerbread)
					mSensorManager.registerListener(gameCanvasView, mSensorManager.getDefaultSensor(0x00000009),
							SensorManager.SENSOR_DELAY_GAME);
				}
			}
		}.start();
		logger.localDebugLog(1, "TIMER", "onStart - loaded sensor: " + (System.currentTimeMillis() - time));
	}

	@Override
	public void onRestart() {
		logger.localDebugLog(1, "StateChnage", "onRestart");
		super.onRestart();
		restartGameThread();

		if (!FallingObjectContainer.bitMapLoaded()) {
			FallingObjectContainer.setUpBitMaps(MAIN_GAME_ACTIVITY);
		}
		if (!BackgroundStars.bitMapLoaded()) {
			BackgroundStars.loadImage(MAIN_GAME_ACTIVITY);
		}
		if (!Bucket.bitMapsLoaded()) {
			Bucket.loadBucketBitmap(MAIN_GAME_ACTIVITY);
		}
		try {
			player.prepare();
			// unPause();
		} catch (final IllegalStateException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		logger.localDebugLog(1, "StateChnage", "onResume");
		super.onResume();
		// final String TAG = "StopResume";
		if (restarting) {
			onPause();
		} else if (catchMe.isPaused() && !catchMe.isGameStopped()) {
			// restartGameThread();
			// player.start();
			// triggerGameAnimation(1000);
			long time2 = System.currentTimeMillis(); // TODO DSJ
			triggerGameAnimation(1000);
			playMusic();
			catchMe.setPaused(false);
			logger.localDebugLog(1, "TIMER", "onResume: " + (System.currentTimeMillis() - time2));

		}

		logger.localDebugLog(1, "TIMER", "onResume: " + (System.currentTimeMillis() - catchMe.time2));
	}

	@Override
	protected void onStop() {
		logger.localDebugLog(1, "StateChnage", "onStop");
		final String TAG = "StopResume";
		logger.localDebugLog(2, TAG, "onStop");
		mSensorManager.unregisterListener(gameCanvasView);
		endExecutors();
		if (!(player == null)) {
			try {
				player.stop();
			} catch (final IllegalStateException e) {
				logger.localDebugLog(2, "MediaPlayer", "IllegalStateException:\n" + e.getCause());
			}
			player.release();
		}
		catchMe.setGameStopped(true);
		if (!catchMe.isGameOver()) { // Chances are they will play again if its
										// just a game over so no need to reload
			if (!BackgroundStars.isBGGame()) {
				BackgroundStars.releaseBitmap();
			}
			FallingObjectContainer.releaseBitMaps();
			Bucket.releaseBitMaps();
		}
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
		final Point starting = new Point();
		final int fromCentre = size.y / 2;
		logger.localDebugLog(2, "Level", "Level: " + catchMe.getLevel());
		switch (catchMe.getLevel()) {
			case 1:
				// Always top
				starting.set(size.x / 2, 0);
				break;
			case 2:
				// top Or Bottom
				final int random = (int) (Math.random() * 2);
				logger.localDebugLog(2, "Level", "Top?: " + random);
				starting.set(size.x / 2, size.y * random);
				break;
			case 3:
				// top or bottom left or right
				final int random3 = (int) (Math.random() * 4);
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
				final int offSetX = size.x / 2;
				final int offSetY = size.y / 2;
				final int angle = (int) (Math.random() * 180);

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
		long time = System.currentTimeMillis(); // TODO DSJ

		logger.localDebugLog(1, "TIMER", "getMGAAct: " + (System.currentTimeMillis() - time)); // TODO
																								// DSJ
		time = System.currentTimeMillis(); // TODO DSJ

		fOContainer = new FallingObjectContainer(MAIN_GAME_ACTIVITY);
		logger.localDebugLog(1, "TIMER", "fOContainer: " + (System.currentTimeMillis() - time)); // TODO
																									// DSJ
		time = System.currentTimeMillis();
		bucket = new Bucket(MAIN_GAME_ACTIVITY);
		logger.localDebugLog(1, "TIMER", "bucket: " + (System.currentTimeMillis() - time)); // TODO
																							// DSJ
		time = System.currentTimeMillis();
		soundPool = new SoundPoolCatchMe(MAIN_GAME_ACTIVITY);
		logger.localDebugLog(1, "TIMER", "soundPool: " + (System.currentTimeMillis() - time)); // TODO
																								// DSJ
		time = System.currentTimeMillis();
		bgStars = new BackgroundStars(MAIN_GAME_ACTIVITY);
		logger.localDebugLog(1, "TIMER", "bgStars: " + (System.currentTimeMillis() - time)); // TODO
																								// DSJ
		time = System.currentTimeMillis();

		gameCanvasView = new GameCanvas(MAIN_GAME_ACTIVITY, MAIN_GAME_ACTIVITY);
		logger.localDebugLog(1, "TIMER", "gCanvasView: " + (System.currentTimeMillis() - time)); // TODO
																									// DSJ

	}

	private void startGame() {
		long time = System.currentTimeMillis(); // TODO DSJ
		
		resetGame();
		logger.localDebugLog(1, "TIMER", "resetGame: " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis(); // TODO DSJ

		

		logger.localDebugLog(1, "TIMER", "loadmusic: " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis(); // TODO DSJ
		makeThreads();
		logger.localDebugLog(1, "TIMER", "makeThreads: " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis(); // TODO DSJ
		makeBars();
		logger.localDebugLog(1, "TIMER", "makeBars: " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis(); // TODO DSJ
		gameExecutor = new ScheduledThreadPoolExecutor(1);
		gameExecutor.scheduleAtFixedRate(gameThread, 0, 1000 / 30, TimeUnit.MILLISECONDS);
		logger.localDebugLog(1, "TIMER", "makegameThread: " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis(); // TODO DSJ
		explosionKiller = new ScheduledThreadPoolExecutor(1);
		explosionKiller.scheduleAtFixedRate(explosionThread, 0, 500, TimeUnit.MILLISECONDS);
		logger.localDebugLog(1, "TIMER", "makeExplosionThread: " + (System.currentTimeMillis() - time));

		// GameState.setCircleDelay(1600);

		// makeExecThread();
		// executorStatus = new ScheduledThreadPoolExecutor(1);
		// executorStatus.scheduleAtFixedRate(execThread, 0, 2,
		// TimeUnit.SECONDS);
		time = System.currentTimeMillis(); // TODO DSJ
		if (catchMe.isMusicON()) {
			new Thread("startmusicThread"){
				public void run() {
					// TODO musicThread.start();
				}
			}.start();
		}
		logger.localDebugLog(1, "TIMER", "StartMusic: " + (System.currentTimeMillis() - time));

	}

	private void restartGameThread() {
		restartGameThread("Innocent Restart from onResume", null);

	}

	private void restartGameThread(String whichMethod, Exception ex) {

		if (ex != null) {
			ex.printStackTrace();
			logger.localDebugLog(2, "restartGameThread", whichMethod + "\n\t" + ex.getMessage());
		}
		if (gameExecutor != null) {
			gameExecutor.shutdownNow();
		}
		gameExecutor = new ScheduledThreadPoolExecutor(1);
		gameExecutor.scheduleAtFixedRate(gameThread, 0, 1000 / 30, TimeUnit.MILLISECONDS);
		catchMe.setGameStopped(false);
	}

	private void update() {
			while (!allTexturesLoaded()) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					logger.localDebugLog(2, "Wait To Start Game", " This won't get Interrupted...ironic");
				}
			}
		final float deltaMilli = getDeltaTimeMilli();
		// playSound('s');
		checkHealth();
		// Check for gone circles
		fOContainer.killDeadCircles();
		try {
			bucket.rotateBucket(catchMe.getRotateDegrees());
		} catch (final Exception ex) {
			restartGameThread("rotateBucket", ex);
		}
		// check any modes
		if (fuelBar.isInUse()) {
			bucket.whiteBucket();
		} else {
			bucket.normalBucket();
		}
		if (BackgroundStars.isBGGame()) {
			try {
				// update BG
				bgStars.update(deltaMilli);
			} catch (final Exception ex) {
				restartGameThread("bgStarsUpdate", ex);
			}
		}
		try {
			if (!catchMe.isPaused()) {
				// update circle positions
				fOContainer.updateCirclePositions(deltaMilli);
			}
		} catch (final Exception ex) {
			restartGameThread("fOCont Update Pos", ex);
		}
		try {
			// update fuel Bars
			fuelBar.update(deltaMilli);
		} catch (final Exception ex) {
			restartGameThread("fuelBarUpdate", ex);
		}
		try {
			healthBar.update(deltaMilli);
		} catch (final Exception ex) {
			restartGameThread("healthBarUpdate", ex);
		}
		try {
			if (!catchMe.isPaused()) {
				checkCollisions();
			}
		} catch (final Exception ex) {
			restartGameThread("checkColl", ex);
		}
		drawSurfaceView();
	}

	private boolean allTexturesLoaded() {
		logger.localDebugLog(
				1,
				"BG || noBGGame,Buck,FO",
				(BackgroundStars.bitMapLoaded() || !BackgroundStars.isBGGame()) + " : " + Bucket.bitMapsLoaded()
						+ " : "
				+ FallingObjectContainer.bitMapLoaded());
		return ((BackgroundStars.bitMapLoaded() || !BackgroundStars.isBGGame()) && Bucket.bitMapsLoaded() && FallingObjectContainer
				.bitMapLoaded());
	}

	private void triggerGameAnimation(int delay) {
		createCircle = new ScheduledThreadPoolExecutor(1);
		createCircle.schedule(circleThread, delay, TimeUnit.MILLISECONDS);
		fRFill = new ScheduledThreadPoolExecutor(1);
		fRFill.scheduleAtFixedRate(fuelRefillThread, delay, 500, TimeUnit.MILLISECONDS);

		// if (!FallingObjectContainer.bitMapsSetUp()) {
		// FallingObjectContainer.setUpBitMaps(MAIN_GAME_ACTIVITY);
		// }
		// if (!BackgroundStars.bitMapLoaded()) {
		// bgStars.loadImage(MAIN_GAME_ACTIVITY);
		// }
	}

	private void drawSurfaceView() {
		// Canvas canvas = surfaceHolder.lockCanvas();

		try {
			// gameCanvasView.postInvalidate();
			gameCanvasView.requestDraw();
		} catch (final Exception ex) {
			restartGameThread("GCV", ex);
		}
		// surfaceHolder.unlockCanvasAndPost(canvas);

	}

	private synchronized void upDateHighScore() {
		final String TAG = "PrefStores";
		// setting preferences
		prefs = MAIN_GAME_ACTIVITY.getSharedPreferences(CatchMe.PREF_FILE_NAME, Context.MODE_PRIVATE);
		editor = prefs.edit();
		logger.localDebugLog(2, TAG, "PUT High Score: " + catchMe.getHighScore());

		final LinkedList<Integer> scores = catchMe.insertAndSortNewHighScore();
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
