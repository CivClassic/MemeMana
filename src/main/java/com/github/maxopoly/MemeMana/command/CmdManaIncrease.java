package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.NameAPI;

public class CmdManaIncrease extends PlayerCommand {
	public CmdManaIncrease(String name) {
		super(name);
		setIdentifier("manaincrease");
		setDescription("Increase someone's mana");
		setUsage("/manaincrease Owner Amount");
		setArguments(2,2);
	}

	public boolean execute(CommandSender sender, String [] args) {
		Integer owner = MemeManaOwnerManager.fromName(args[0]);
		if(owner == null) {
			sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid mana owner");
			return false;
		}
		Integer giveAmount = null;
		try {
			giveAmount = Integer.parseInt(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a valid amount of mana");
			return false;
		}
		MemeManaPouch.getPouch(owner).addMana(giveAmount, NameAPI.getUUID(args[0]));
		sender.sendMessage(ChatColor.GREEN + "Increased the mana of " + ChatColor.AQUA + args[0] + ChatColor.GREEN + " by " + ChatColor.GOLD + giveAmount);
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
