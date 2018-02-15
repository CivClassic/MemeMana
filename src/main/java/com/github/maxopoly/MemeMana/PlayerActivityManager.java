package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.civclassic.altmanager.AltManager;
import org.bukkit.Bukkit;
import java.util.Map;
import java.util.UUID;
import java.util.Date;

public class PlayerActivityManager {

	private Map<Integer, ManaGainStat> stats;

	public PlayerActivityManager() {
		reloadFromDB();
	}

	public void reloadFromDB() {
		this.stats = MemeManaPlugin.getInstance().getDAO().getManaStats();
	}

	public ManaGainStat getForPlayer(int oid) {
		stats.putIfAbsent(oid,new ManaGainStat());
		ManaGainStat stat = stats.get(oid);
		MemeManaPlugin.getInstance().getDAO().updateManaStat(oid,stat);
		return stat;
	}

	public void updatePlayer(UUID player) {
		int owner = MemeManaOwnerManager.fromUUID(player);
		ManaGainStat stat = getForPlayer(owner);
		Bukkit.getPlayer(player).sendMessage("DEBUG MESSAGE: timeout is " + MemeManaPlugin.getInstance().getManaConfig().getManaGainTimeout() + ", lastDay is " + stat.getLastDay() + ", now is " + new Date().getTime());
		if(stat.update()) {
			MemeManaPlugin.getInstance().getDAO().updateManaStat(owner,stat);
			giveOutReward(player,stat.getStreak());
		}
		else {
			Bukkit.getPlayer(player).sendMessage("You didn't get any mana");
		}
	}

	public void giveOutReward(UUID player, int amount) {
		MemeManaPouch.getPouch(MemeManaOwnerManager.fromUUID(player)).addMana(amount);
		Bukkit.getPlayer(player).sendMessage("You got " + amount + " mana for logging in");
	}
}
