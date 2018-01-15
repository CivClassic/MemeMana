package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import java.util.Map;
import java.util.UUID;

public class PlayerActivityManager {

	// Maps a single player in an alt-group to their ManaGainStat
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
		ManaGainStat relevantAlt = stats.get(MemeManaIdentity.selectAlt(stats.keySet(),player));
		if(relevantAlt.update()) {
			giveOutReward(player,relevantAlt.getStreak());
		}
	}

	public void giveOutReward(UUID player, int amount) {
		MemeManaPlugin.getInstance().getManaManager().getPouch(player).addNewUnit(manaManager.getNextManaID(), amount);
		// TODO send message to player?
	}
}
