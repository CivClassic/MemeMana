package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import java.util.Date;

public class ManaGainStat {

	// Bit field. Left -> Right is forward in time, right most position is lastDay
	private int streak;
	private long lastDay;

	public ManaGainStat() {
		this.streak = 0;
		this.lastDay = 0L;
	}

	public ManaGainStat(int streak, long lastDay) {
		this.streak = streak;
		this.lastDay = lastDay;
	}

	/**
	 * Updates the streak and last registered day. If the day has changed, meaning the stats were actually updated, true
	 * will be returned, false otherwise
	 *
	 * @return True if they should get mana
	 */
	public boolean update() {
		long currentMillis = System.currentTimeMillis();
		long serverRestartOffset = MemeManaPlugin.getInstance().getManaConfig.getServerRestartOffset();
		long currentDay = (currentMillis - serverRestartOffset) / 86400000L;
		long lastLoginDay = (lastDay - serverRestartOffset) / 86400000L;
		long daysPast = Math.max(0L,currentDay - lastDay);
		if (daysPast < 1) {
			return false;
		}
		streak = ((streak << daysPast) | 1) & maxMask();
		lastDay = currentMillis;
		return true;
	}

	private int maxMask() {
		return ~((~0) << MemeManaPlugin.getInstance().getManaConfig().getMaximumDailyMana());
	}

	public int getPayout() {
		return Integer.bitCount(streak);
	}

	public int getStreakField() {
		return streak;
	}

	public long getLastDay() {
		return lastDay;
	}

	public void reset() {
		this.streak = 0;
	}
}
