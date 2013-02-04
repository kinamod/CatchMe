/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.kinamod.catchme.R.raw;
import com.kinamod.catchme.activities.MainGameActivity;

public class SoundPoolCatchMe {
	boolean loaded = false;
	private int[] soundID = new int[9];
	private SoundPool soundPool;
	float volume;

	public SoundPoolCatchMe(MainGameActivity activity) {
		AudioManager audioManager = (AudioManager) activity
				.getSystemService(Context.AUDIO_SERVICE);
		volume = setUpVolume(audioManager);

		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		loadMusic(soundPool, activity);
	}

	private void loadMusic(SoundPool sndPool, MainGameActivity activity) {
		soundID[0] = sndPool.load(activity, raw.beep_deep, 1);
		soundID[1] = sndPool.load(activity, raw.beep_short_l, 1);
		soundID[2] = sndPool.load(activity, raw.zapped, 1);
		soundID[3] = sndPool.load(activity, raw.bing_bong, 1);
		soundID[4] = sndPool.load(activity, raw.lazer285, 1);
		soundID[5] = sndPool.load(activity, raw.gunfire, 1);
		// soundID[6] = sndPool.load(activity, raw.scream_0, 1);
		// soundID[7] = sndPool.load(activity, raw.scream_1, 1);
		// soundID[8] = sndPool.load(activity, raw.scream_2, 1);
	}

	public void playSound(int soundNumber) {
		soundPool.play(soundID[soundNumber], volume, volume, 1, 0, 1);
	}

	private float setUpVolume(AudioManager audioManager) {
		float actualVolume = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		return actualVolume / maxVolume;
	}
}
