package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import java.util.Map;
import java.util.UUID;

public class PlayerActivityManager {

	private Map<UUID, ManaGainStat> stats;
	private MemeManaManager manaManager;

	public PlayerActivityManager(MemeManaManager manaManager) {
		this.manaManager = manaManager;
		reloadFromDB();
	}

	public void reloadFromDB() {
		// TODO
	}

	public void updatePlayer(UUID player) {
		// TODO altmanager integration
		ManaGainStat stat = stats.get(player);
		if (stat.update()) {
			giveOutReward(player, stat.getStreak());
		}
	}

	public void giveOutReward(UUID player, int amount) {
		MemeManaPlugin.getInstance().getManaManager().getPouch(player).addNewUnit(manaManager.getNextManaID(), amount);
		// TODO send message to player?
	}
}
