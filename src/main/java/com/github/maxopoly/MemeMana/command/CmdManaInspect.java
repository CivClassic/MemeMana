package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaGUI;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaInspect extends PlayerCommand {
	public CmdManaInspect(String name) {
		super(name);
		setIdentifier("manainspect");
		setDescription("Inspect someone's mana");
		setUsage("/manainspect Owner");
		setArguments(1,1);
	}

	@Override
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
		if(stat.getPayout() != 0) {
			sender.sendMessage(ChatColor.AQUA + args[0] + ChatColor.YELLOW + "'s mana streak is " + ChatColor.LIGHT_PURPLE + stat.getPayout());
		}
		if(sender instanceof Player){
			UUID playerId = ((Player) sender).getUniqueId();
			MemeManaGUI<Long> gui = new MemeManaGUI<Long>(playerId,() -> pouch.getRawUnits().keySet(),pouch::getDisplayStack,(timestamp,p) -> {});
			gui.showScreen();
		}
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
