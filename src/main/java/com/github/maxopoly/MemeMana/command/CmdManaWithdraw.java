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
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.GroupManager;

public class CmdManaWithdraw extends PlayerCommand {
	public CmdManaWithdraw(String name) {
		super(name);
		setIdentifier("manawithdraw");
		setDescription("Withdraw some mana from a namelayer group");
		setUsage("/manawithdraw Group Amount");
		setArguments(2,2);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			msg("Can't withdraw mana from console");
			return true;
		}
		Player player = (Player) sender;
		MemeManaPouch toPouch = MemeManaPouch.getPouch(MemeManaOwnerManager.fromPlayer(player));
		Group nlGroup = GroupManager.getGroup(args[0]);
		if(nlGroup == null){
			msg("<c>%s <b>is not a valid namelayer group",args[0]);
			return false;
		}
		MemeManaPouch fromPouch = MemeManaPouch.getPouch(MemeManaOwnerManager.fromNameLayerGroup(nlGroup));
		int transferAmount = fromPouch.getManaContent();
		if (args.length == 2) {
			try {
				transferAmount = Integer.parseInt(args[1]);
			} catch (Exception e) {
				msg("<i>%s <b>is not a valid amount of mana",args[1]);
				return false;
			}
		}
		if (fromPouch.transferMana(toPouch,transferAmount)) {
			msg("<g>You withdrew <i>%s<g> mana from the group <i>%s","" + transferAmount,args[0]);
			return true;
		}
		msg("<b>Mana transfer unsuccessful. Make sure you have enough mana available");
		return true;
	}

	public List<String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
