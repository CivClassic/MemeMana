package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.ExilePearl;
import com.civclassic.altmanager.AltManager;
import org.bukkit.Bukkit;
import java.util.Map;
import java.util.UUID;
import java.util.Date;
import net.md_5.bungee.api.ChatColor;

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
		}
	}

	private void giveOutReward(UUID player, int streakPayout) {
		if(ExilePearlPlugin.getApi().isPlayerExiled(player)){
			// streakPayout is in [1,max] so we subtract one to get proper scaling behavior
			int damage = MemeManaPlugin.getInstance().getManaConfig().getPearlDamageCurveInitial() + MemeManaPlugin.getInstance().getManaConfig().getPearlDamageCurveIncrement() * (streakPayout - 1);
			if(damage > 0){
				ExilePearl pearl = ExilePearlPlugin.getApi().getPearl(player);
				int oldHealth = pearl.getHealth();
				pearl.setHealth(oldHealth - damage);
				Bukkit.getPlayer(player).sendMessage(ChatColor.DARK_GREEN + "You damaged your pearl for " + ChatColor.GOLD + Math.min(oldHealth,damage) + ChatColor.DARK_GREEN + " health points by logging in");
			}
		}else{
			MemeManaPouch.getPouch(MemeManaOwnerManager.fromUUID(player)).addMana(streakPayout, player);
			Bukkit.getPlayer(player).sendMessage(ChatColor.DARK_GREEN + "You got " + ChatColor.GOLD + streakPayout + ChatColor.DARK_GREEN + " mana for logging in");
		}
	}
}
