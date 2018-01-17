package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
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

	private ManaGainStat getForPlayer(MemeManaOwner ident) {
		Long pid = ident.selectAlt(stats.keySet());
		if(pid != null) {
			return stats.get(pid);
		} else {
			ManaGainStat stat = new ManaGainStat(0,0);
			stats.put(ident.getID(),stat);
			MemeManaPlugin.getInstance().getDAO().addManaStat(ident,stat);
			return stat;
		}
	}

	public void updatePlayer(MemeManaOwner player) {
		ManaGainStat relevantAlt = getForPlayer(player);
		if(relevantAlt.update()) {
			giveOutReward(player,relevantAlt.getStreak());
		}
	}

	public void giveOutReward(MemeManaOwner player, int amount) {
		MemeManaPlugin.getInstance().getManaManager().addMana(player,amount);
		// TODO send message to player?
	}
}
