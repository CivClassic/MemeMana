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
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;

public class CmdManaInspect extends PlayerCommand {
	public CmdManaInspect(String name) {
		super(name);
		setIdentifier("manainspect");
		setDescription("Inspect someone's mana");
		setUsage("/manainspect Owner");
		setArguments(1,1);
	}

	public boolean execute(CommandSender sender, String [] args) {
		Integer owner = MemeManaOwnerManager.fromName(args[0]);
		if(owner == null) {
			sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid mana owner");
			return false;
		}
		MemeManaPouch pouch = MemeManaPouch.getPouch(owner);
		int manaAvailable = pouch.getManaContent();
		sender.sendMessage(ChatColor.AQUA + args[0] + ChatColor.YELLOW + " has " + ChatColor.GOLD + manaAvailable + ChatColor.YELLOW + " mana");
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		if(stat.getStreak() != 0) {
			sender.sendMessage(ChatColor.AQUA + args[0] + ChatColor.YELLOW + " is on a " + ChatColor.LIGHT_PURPLE + stat.getStreak() + ChatColor.YELLOW + " day login streak");
		}
		if(sender instanceof Player){
			UUID playerId = ((Player) sender).getUniqueId();
			MemeManaGUI<Long> gui = new MemeManaGUI<Long>(playerId,() -> pouch.getRawUnits().keySet(),pouch::getDisplayStack,(timestamp,p) -> {});
			gui.showScreen();
		}
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
