package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class CmdManaShow extends PlayerCommand {
	public CmdManaShow(String name) {
		super(name);
		setIdentifier("manashow");
		setDescription("Show your own mana or the mana of a group you have access to");
		setUsage("/manashow or /manashow <GROUP>");
		setArguments(0,1);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player) && args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Can't show your own mana from console.");
			return true;
		}

		if (args.length > 0)
		{
			Group nlGroup = GroupManager.getGroup(args[0]);
			if (nlGroup == null)
			{
				sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid NameLayer group");
				return true;
			}

			if(!NameAPI.getGroupManager().hasAccess(nlGroup, ((Player) sender).getUniqueId(), PermissionType.getPermission(CmdManaWithdraw.withdrawPermissionName))){
				sender.sendMessage(ChatColor.RED + "You don't have permission to view mana from " + ChatColor.AQUA + args[0]);
				return true;
			}

			int owner = MemeManaOwnerManager.fromNameLayerGroup(nlGroup);
			int manaAvailable = MemeManaPouch.getPouch(owner).getManaContent();

			sender.sendMessage(ChatColor.GOLD + args[0] + " has " + ChatColor.GOLD + manaAvailable + ChatColor.YELLOW + " mana");
		}
		else
		{
			int owner = MemeManaOwnerManager.fromPlayer((Player)sender);
			int manaAvailable = MemeManaPouch.getPouch(owner).getManaContent();
			sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GOLD + manaAvailable + ChatColor.YELLOW + " mana");
			ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
			if(stat.getPayout() != 0) {
				sender.sendMessage(ChatColor.YELLOW + "Your mana streak is " + ChatColor.LIGHT_PURPLE + stat.getPayout());
			}
		}
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
