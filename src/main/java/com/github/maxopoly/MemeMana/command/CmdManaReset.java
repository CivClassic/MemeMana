package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

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
