package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.MemeManaPlayerOwner;
import com.civclassic.altmanager.AltManager;
import org.bukkit.Bukkit;
import java.util.Map;
import java.util.UUID;

public class PlayerActivityManager {

	private Map<Integer, ManaGainStat> stats;
	private MemeManaManager manaManager;

	public PlayerActivityManager(MemeManaManager manaManager) {
		this.manaManager = manaManager;
		reloadFromDB();
	}

	public void reloadFromDB() {
		this.stats = MemeManaPlugin.getInstance().getDAO().getManaStats();
	}

	public ManaGainStat getForPlayer(MemeManaPlayerOwner ident) {
		int oid = ident.getID();
		stats.putIfAbsent(oid,new ManaGainStat());
		ManaGainStat stat = stats.get(oid);
		MemeManaPlugin.getInstance().getDAO().updateManaStat(ident,stat);
		return stat;
	}

	public void updatePlayer(UUID player) {
		MemeManaPlayerOwner owner = MemeManaPlayerOwner.fromUUID(player);
		ManaGainStat stat = getForPlayer(owner);
		if(stat.update()) {
			MemeManaPlugin.getInstance().getDAO().updateManaStat(owner,stat);
			giveOutReward(player,stat.getStreak());
		}
		else {
			Bukkit.getPlayer(player).sendMessage("You didn't get any mana");
		}
	}

	public void giveOutReward(UUID player, int amount) {
		MemeManaPlugin.getInstance().getManaManager().addMana(MemeManaPlayerOwner.fromUUID(player),amount);
		Bukkit.getPlayer(player).sendMessage("You got " + amount + " mana for logging in");
	}
}
