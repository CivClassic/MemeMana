package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.NameAPI;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.PearlType;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.UUID;
import java.util.List;
import java.util.LinkedList;
import java.util.function.IntFunction;
import java.text.DecimalFormat;
import net.md_5.bungee.api.ChatColor;

public class CmdManaShow extends PlayerCommand {
	public CmdManaShow(String name) {
		super(name);
		setIdentifier("manashow");
		setDescription("Show your own mana");
		setUsage("/manashow");
		setArguments(0,0);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't show your own mana from console.");
			return true;
		}
		int owner = MemeManaOwnerManager.fromPlayer((Player)sender);
		int manaAvailable = MemeManaPouch.getPouch(owner).getManaContent();
		sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GOLD + manaAvailable + ChatColor.YELLOW + " mana");
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		if(stat.getStreak() != 0) {
			sender.sendMessage(ChatColor.YELLOW + "You are on a " + ChatColor.LIGHT_PURPLE + stat.getStreak() + ChatColor.YELLOW + " day login streak");
		}
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
