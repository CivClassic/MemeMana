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
import net.md_5.bungee.api.ChatColor;

public class CmdManaReset extends PlayerCommand {
	public CmdManaReset(String name) {
		super(name);
		setIdentifier("manareset");
		setDescription("Reset a player's mana stats");
		setUsage("/manareset Player");
		setArguments(1,1);
	}

	public boolean execute(CommandSender sender, String [] args) {
		Integer owner = MemeManaOwnerManager.fromPlayerName(args[0]);
		if(owner == null) {
			sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid player");
			return false;
		}
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		stat.reset();
		MemeManaPlugin.getInstance().getDAO().updateManaStat(owner,stat);
		sender.sendMessage(ChatColor.GREEN + "Reset mana statistics for " + ChatColor.AQUA + args[0]);
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
