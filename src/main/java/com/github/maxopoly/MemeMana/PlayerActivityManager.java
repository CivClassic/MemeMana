package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import java.util.Map;
import java.util.UUID;

import com.programmerdan.minecraft.banstick.data.BSPlayer;

public class PlayerActivityManager {

	private Map<Long, ManaGainStat> stats;
	private MemeManaManager manaManager;

	public PlayerActivityManager(MemeManaManager manaManager) {
		this.manaManager = manaManager;
		reloadFromDB();
	}

	public void reloadFromDB() {
		this.stats = MemeManaPlugin.getInstance().getDAO().getManaStats();
	}

	private ManaGainStat getForPlayer(UUID player) {
		MemeManaIdentity ident = new MemeManaIdentity(player);
		ManaGainStat stat = stats.get(ident.selectAlt(stats.keySet()));
		if(stat == null) {
			stat = new ManaGainStat(0,0);
			stats.put(ident.getID(),stat);
			MemeManaPlugin.getInstance().getDAO().addManaStat(ident,stat);
		}
		return stat;
	}

	public void updatePlayer(UUID player) {
		ManaGainStat relevantAlt = getForPlayer(player);
		if(relevantAlt.update()) {
			giveOutReward(player,relevantAlt.getStreak());
		}
	}

	public void giveOutReward(UUID player, int amount) {
		MemeManaPlugin.getInstance().getManaManager().getPouch(player).addNewUnit(manaManager.getNextManaID(), amount);
		// TODO send message to player?
	}
}
