package com.kinamod.catchme.util;

import android.widget.Toast;

import com.kinamod.catchme.R;
import com.kinamod.catchme.activities.HomeScreenActivity;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser;
import com.swarmconnect.delegates.SwarmLoginListener;

public class SwarmHandler {

	private static HomeScreenActivity hSA = HomeScreenActivity.getInstance();
	public static final int SWARM_ACH_OVER10 = 9219;
	public static final int SWARM_ACH_OVER50 = 9228;
	public static final int SWARM_ACH_OVER100 = 9231;
	public static final int SWARM_ACH_OVER200 = 9297;
	public static final String SWARM_KEY = "2dfc6c0404b19b5ae69eaf28db852ac0";
	public static final int SWARM_ID = 3732;
	public static final int LDBOARD_ONE = 5808;

	private static boolean loggedIn;
	private static boolean loginOffered = false;

	// METHODS = = = = = = = = = = = = = = = = = = = = = =
	public static boolean isLoggedIn() {
		return loggedIn;
	}

	public static void setLoggedIn(boolean loggedIn) {
		SwarmHandler.loggedIn = loggedIn;
	}

	public static boolean isLoginOffered() {
		return loginOffered;
	}

	public static void setLoginOffered(boolean loginOffer) {
		SwarmHandler.loginOffered = loginOffer;
	}

	// Listeners = = = = = = = = = = = = = = = = = = =
	public static SwarmLoginListener swarmLoginListener = new SwarmLoginListener() {

		// This method is called when the login process has started
		// (when a login dialog is displayed to the user).
		public void loginStarted() {
		}

		// This method is called if the user cancels the login process.
		public void loginCanceled() {
		}

		// This method is called when the user has successfully logged in.
		public void userLoggedIn(SwarmActiveUser user) {
			setLoggedIn(true);
			Toast toast = Toast.makeText(hSA, hSA.getResources().getString(R.string.textLoggedInAs)
					+ Swarm.user.username, Toast.LENGTH_SHORT);
			toast.show();
			ScoreHandler.loadAchievements();
		}

		// This method is called when the user logs out.
		public void userLoggedOut() {
			setLoggedIn(false);
		}

	};
}
