package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import java.util.Map;
import java.util.UUID;

public class PlayerActivityManager {

	private Map<UUID, ManaGainStat> stats;

	public void updatePlayer(UUID player) {
		// TODO altmanager integration
		ManaGainStat stat = stats.get(player);
		if (stat.update()) {
			giveOutReward(stat.getStreak());
		}
	}

	public void giveOutReward(int amount) {
		// TODO
	}

}
