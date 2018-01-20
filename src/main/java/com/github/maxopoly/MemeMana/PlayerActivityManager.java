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

	private ManaGainStat getForPlayer(UUID ident) {
		int oid = AltManager.instance().getAssociationGroup(ident);
		stats.putIfAbsent(oid,new ManaGainStat(0,0));
		ManaGainStat stat = stats.get(oid);
		MemeManaPlugin.getInstance().getDAO().updateManaStat(MemeManaPlayerOwner.fromUUID(ident),stat);
		return stat;
	}

	public void updatePlayer(UUID player) {
		ManaGainStat stat = getForPlayer(player);
		if(stat.update()) {
			MemeManaPlugin.getInstance().getDAO().updateManaStat(MemeManaPlayerOwner.fromUUID(player),stat);
			giveOutReward(player,stat.getStreak());
		}
	}

	public void giveOutReward(UUID player, int amount) {
		MemeManaPlugin.getInstance().getManaManager().addMana(MemeManaPlayerOwner.fromUUID(player),amount);
		Bukkit.getPlayer(player).sendMessage("Gave you " + amount + " mana");
	}
}
