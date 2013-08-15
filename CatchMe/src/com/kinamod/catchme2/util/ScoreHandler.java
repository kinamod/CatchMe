package com.kinamod.catchme2.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.kinamod.catchme2.CatchMe;
import com.kinamod.catchme2.R;
import com.kinamod.catchme2.activities.HomeScreenActivity;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmAchievement;
import com.swarmconnect.SwarmAchievement.GotAchievementsMapCB;
import com.swarmconnect.SwarmLeaderboard;

public class ScoreHandler {
	SwarmHandler swarmHand = new SwarmHandler();
	private static HomeScreenActivity hSA;

	@SuppressLint("UseSparseArrays")
	private static Map<Integer, SwarmAchievement> achievements = new HashMap<Integer, SwarmAchievement>();
	CatchMe catchMe = CatchMe.getInstance();
	CustomisedLogging logger = new CustomisedLogging(false, false);

	public ScoreHandler(HomeScreenActivity hsa) {
		hSA = hsa;
	}

	public void checkAcheivements() {
		if (!SwarmHandler.isLoggedIn()) {
			return;
		}
		if (catchMe.getScore() >= 10) {

			// Check for nulls in case the user is offline
			if (achievements != null
					&& !achievements.containsKey(SwarmHandler.SWARM_ACH_OVER10)) {

				// Give the achievement for successfully landing!
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER10);

				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast10), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 50) {
			if (achievements != null
					&& !achievements.containsKey(SwarmHandler.SWARM_ACH_OVER50)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER50);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast50), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 100) {
			if (achievements != null
					&& !achievements
							.containsKey(SwarmHandler.SWARM_ACH_OVER100)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER100);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast100), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 200) {
			if (achievements != null
					&& !achievements
							.containsKey(SwarmHandler.SWARM_ACH_OVER200)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER200);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast200), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 500) {
			if (achievements != null
					&& !achievements
							.containsKey(SwarmHandler.SWARM_ACH_OVER500)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER500);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast500), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 1000) {
			if (achievements != null
					&& !achievements
							.containsKey(SwarmHandler.SWARM_ACH_OVER1000)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER1000);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast1000), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 2000) {
			if (achievements != null
					&& !achievements
							.containsKey(SwarmHandler.SWARM_ACH_OVER2000)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER2000);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast2000), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 10000) {
			if (achievements != null
					&& !achievements
							.containsKey(SwarmHandler.SWARM_ACH_OVER10000)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER10000);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast10000), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 20000) {
			if (achievements != null
					&& !achievements
							.containsKey(SwarmHandler.SWARM_ACH_OVER20000)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER20000);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast20000), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (catchMe.getScore() >= 50000) {
			if (achievements != null
					&& !achievements
							.containsKey(SwarmHandler.SWARM_ACH_OVER50000)) {
				SwarmAchievement.unlock(SwarmHandler.SWARM_ACH_OVER50000);
				final Toast toast = Toast.makeText(hSA, hSA.getResources()
						.getString(R.string.textPast50000), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public void makeHighScoreDialog(String inString, boolean gameOvr) {
		final LinkedList<Integer> scores = catchMe.getHighScores();
		final StringBuilder sb = new StringBuilder();
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
			sb.append("\n\n"
					+ hSA.getResources().getString(R.string.textYourScore)
					+ "  " + catchMe.getScore());
		}
		final AlertDialog.Builder aDBuilder = new AlertDialog.Builder(hSA);

		if (Swarm.user == null) {
			aDBuilder
					.setTitle(inString)
					.setMessage(sb.toString())
					.setPositiveButton(
							hSA.getResources().getString(
									R.string.textSwarmLogin), swarmLogin)
					.create().show();
		} else {
			inString = inString.concat(":  " + Swarm.user.username);
			if (!Swarm.user.isGuestAccount()) {
				if (!catchMe.isScoreSubmitted() && catchMe.isFirstPlayed()) {
					aDBuilder
							.setTitle(inString)
							.setMessage(sb.toString())
							.setPositiveButton(
									hSA.getResources().getString(
											R.string.textSubmitScore),
									swarmSubmitScore).create().show();
				} else {
					aDBuilder
							.setTitle(inString)
							.setMessage(sb.toString())
							.setPositiveButton(
									hSA.getResources().getString(
											R.string.textGlobalLeaderboards),
									swarmLeaderboards).create().show();
				}
			} else {
				aDBuilder
						.setTitle(inString)
						.setMessage(sb.toString())
						.setPositiveButton(
								hSA.getResources().getString(
										R.string.textSwarmLoginFullUser),
								swarmLogin)
						.setNegativeButton(
								hSA.getResources().getString(
										R.string.textGlobalLeaderboards),
								swarmLeaderboards).create().show();
			}
		}
	}

	// Listeners ===================================
	DialogInterface.OnClickListener swarmSubmitScore = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dia, int id) {
			catchMe.setScoreSubmitted(true);
			SwarmLeaderboard.submitScore(SwarmHandler.LDBOARD_ONE,
					catchMe.getScore());

			Swarm.showLeaderboards();
		}
	};
	DialogInterface.OnClickListener swarmLeaderboards = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dia, int id) {
			Swarm.showLeaderboards();
		}
	};
	DialogInterface.OnClickListener swarmLogin = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dia, int id) {
			// start to init swarm now...cheater
			Swarm.logOut();
			Swarm.init(hSA, SwarmHandler.SWARM_ID, SwarmHandler.SWARM_KEY,
					SwarmHandler.swarmLoginListener);
			// Swarm.init(hSA, SwarmHandler.SWARM_ID, SwarmHandler.SWARM_KEY,
			// SwarmHandler.swarmLoginListener);
		}
	};
	DialogInterface.OnClickListener swarmLogout = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dia, int id) {
			// start to init swarm now...cheater
			Swarm.logOut();
		}
	};

	public static void loadAchievements() {
		// Load achievements so they are
		// available when we need them later.
		SwarmAchievement.getAchievementsMap(new GotAchievementsMapCB() {
			@Override
			public void gotMap(Map<Integer, SwarmAchievement> achievementsMap) {

				// Store the map of achievements to be used later.
				achievements = achievementsMap;
				// Toast toast = Toast.makeText(activity,
				// activity.getResources()
				// .getString(R.string.textGotAchievements),
				// Toast.LENGTH_SHORT);
				// toast.show();
			}
		});
	}

}
