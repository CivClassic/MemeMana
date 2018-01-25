package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaPlugin;

public class ManaGainStat {

	private int streak;
	private long lastDay;

	public ManaGainStat() {
		this.streak = 0;
		this.lastDay = 0;
	}

	public ManaGainStat(int streak, long lastDay) {
		this.streak = streak;
		this.lastDay = lastDay;
	}

	/**
	 * Updates the streak and last registered day. If the day has changed, meaning the stats were actually updated, true
	 * will be returned, false otherwise
	 *
	 * @return True if the day has changed since the last check
	 */
	public boolean update() {
		long currentDay = getDayFromTimeStamp(System.currentTimeMillis());
		if (currentDay == lastDay) {
			return false;
		}
		if (currentDay == lastDay + 1) {
			streak = Math.min(streak + 1, MemeManaPlugin.getInstance().getManaConfig().getMaximumDailyMana());
		} else {
			streak = 1;
		}
		lastDay = currentDay;
		return true;
	}

	public int getStreak() {
		return streak;
	}

	public long getLastDay() {
		return lastDay;
	}

	public void reset() {
		this.streak = 0;
		this.lastDay = 0;
	}

	public static long getDayFromTimeStamp(long timeStamp) {
		return timeStamp / (24 * 60 * 60 * 1000);
	}

}
