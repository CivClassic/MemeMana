package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import java.util.Date;

public class ManaGainStat {

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
		long currentDay = new Date().getTime();
		if (currentDay - lastDay < MemeManaPlugin.getInstance().getManaConfig().getManaGainTimeout()) {
			return false;
		}
		if (currentDay - lastDay < (MemeManaPlugin.getInstance().getManaConfig().getManaGainTimeout() * 2L)) {
			streak = Math.min(streak + 1, MemeManaPlugin.getInstance().getManaConfig().getMaximumDailyMana());
			lastDay = currentDay;
			return true;
		}
		streak = Math.max(1,streak - (int) (currentDay - lastDay));
		lastDay = currentDay;
		return true;
	}

	public int getStreak() {
		return streak;
	}

	public long getLastDay() {
		return lastDay;
	}

	public long millisToNextGain() {
		return MemeManaPlugin.getInstance().getManaConfig().getManaGainTimeout() - (new Date().getTime() - lastDay);
	}

	public void reset() {
		this.streak = 0;
		this.lastDay = 0;
	}
}
