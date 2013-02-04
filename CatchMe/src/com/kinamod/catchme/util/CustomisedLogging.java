/*
 * com.kinamod.catchme.util.SoundPoolCatchMe
 * 
 * Version 1.0
 *
 * @author Domanic Smith-Jones
 */
package com.kinamod.catchme.util;

import android.util.Log;

public class CustomisedLogging {
	boolean logOne, logTwo;

	public CustomisedLogging(boolean one, boolean two) {
		logOne = one;
		logTwo = two;
	}

	public void localDebugLog(int type, String TAG, String message) {
		switch (type) {
		case 1:
			if (logOne) {
				Log.i(TAG, message);
			}
			break;
		case 2:
			if (logTwo) {
				Log.i(TAG, message);
			}
			break;
		}

	}
}
