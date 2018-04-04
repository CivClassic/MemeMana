package com.github.maxopoly.MemeMana;

import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

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
		if(stat.update()) {
			MemeManaPlugin.getInstance().getDAO().updateManaStat(owner,stat);
			if(ExilePearlPlugin.getApi().isPlayerExiled(player)){
				Bukkit.getPlayer(player).sendMessage(ChatColor.GRAY + "You didn't get any mana because you are pearled");
			}else{
				giveOutReward(player,stat.getPayout());
			}
		}else{
			Bukkit.getPlayer(player).sendMessage(ChatColor.GRAY + "You didn't get any mana because you already recieved mana today");
		}
	}

	public void giveOutReward(UUID player, int amount) {
		MemeManaPouch.getPouch(MemeManaOwnerManager.fromUUID(player)).addMana(amount, player);
		Bukkit.getPlayer(player).sendMessage(ChatColor.DARK_GREEN + "You got " + ChatColor.GOLD + amount + ChatColor.DARK_GREEN + " mana for logging in");
	}
}
