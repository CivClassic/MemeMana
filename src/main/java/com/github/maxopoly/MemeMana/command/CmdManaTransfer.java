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

public class CmdManaTransfer extends PlayerCommand {
	public CmdManaTransfer(String name) {
		super(name);
		setIdentifier("manatransfer");
		setDescription("Transfer some of your mana to someone else");
		setUsage("/manatransfer Owner Amount");
		setArguments(2,2);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't transfer mana from console");
			return true;
		}
		Player player = (Player) sender;
		Integer transferTo = MemeManaOwnerManager.fromName(args[0]);
		if (transferTo == null) {
			sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid mana owner");
			return false;
		}
		if(MemeManaOwnerManager.fromPlayer(player) == transferTo){
			sender.sendMessage(ChatColor.RED + "Can't transfer mana to yourself");
			return false;
		}
		
		MemeManaPouch fromPouch = MemeManaPouch.getPouch(MemeManaOwnerManager.fromPlayer(player));
		int transferAmount = fromPouch.getManaContent();
		if (args.length == 2) {
			try {
				transferAmount = Integer.parseInt(args[1]);
				if(transferAmount <= 0){
					throw new NumberFormatException();
				}
			} catch (Exception e) {
				sender.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a valid amount of mana");
				return false;
			}
		}
		if (fromPouch.transferMana(MemeManaPouch.getPouch(transferTo),transferAmount)) {
			sender.sendMessage(ChatColor.GREEN + "You transferred " + ChatColor.AQUA + transferAmount + ChatColor.GREEN + " mana to " + ChatColor.AQUA + args[0]);
			return true;
		}
		sender.sendMessage(ChatColor.RED + "Mana transfer unsuccessful; Make sure you have enough mana available");
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
