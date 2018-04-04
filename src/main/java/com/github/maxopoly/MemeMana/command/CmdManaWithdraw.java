package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class CmdManaWithdraw extends PlayerCommand {
	public static final String withdrawPermissionName = "MEMEMANA_WITHDRAW";
	public CmdManaWithdraw(String name) {
		super(name);
		setIdentifier("manawithdraw");
		setDescription("Withdraw some mana from a namelayer group");
		setUsage("/manawithdraw <Group> [Amount]");
		setArguments(1,2);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't withdraw mana from console");
			return true;
		}
		Player player = (Player) sender;
		MemeManaPouch toPouch = MemeManaPouch.getPouch(MemeManaOwnerManager.fromPlayer(player));
		Group nlGroup = GroupManager.getGroup(args[0]);
		if(nlGroup == null){
			sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid NameLayer group");
			return false;
		}
		MemeManaPouch fromPouch = MemeManaPouch.getPouch(MemeManaOwnerManager.fromNameLayerGroup(nlGroup));
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
		if(!NameAPI.getGroupManager().hasAccess(nlGroup, player.getUniqueId(), PermissionType.getPermission(withdrawPermissionName))){
			sender.sendMessage(ChatColor.RED + "You don't have permission to withdraw mana from " + ChatColor.AQUA + args[0]);
			return true;
		}

		if (fromPouch.transferMana(toPouch,transferAmount)) {
			sender.sendMessage(ChatColor.GREEN + "You withdrew " + ChatColor.GOLD + transferAmount + ChatColor.GREEN + " mana from the group " + ChatColor.AQUA + args[0]);
			return true;
		}
		sender.sendMessage(ChatColor.RED + "Mana withdraw unsuccessful; Make sure you have enough mana in the group");
		return true;
	}

	public List<String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
