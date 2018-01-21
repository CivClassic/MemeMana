package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.NameAPI;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.PearlType;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.MemeManaPlayerOwner;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.UUID;
import java.util.List;
import java.util.LinkedList;
import java.util.function.IntFunction;

public class CmdManaAdmin extends PlayerCommand {
	public CmdManaAdmin(String name) {
		super(name);
		setIdentifier("manaadmin");
		setDescription("The admin command for Meme Mana");
		setUsage("\n/manaadmin inspect Player -- Show a player's current mana\n/manaadmin give -- Give a player some mana\n/mana reset <Player> -- Reset a player's streak");
		setArguments(1,3);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (args[0].equals("inspect")) {
			inspectManaAmount(sender,args);
		} else if (args[0].equals("give")) {
			doGive(sender,args);
		} else if (args[0].equals("reset")) {
			doReset(sender,args);
		// NYI
		//} else if (args[0].equals("transferany")) {
			//doTransfer(sender,args);
		}
		return true;
	}

	public void inspectManaAmount(CommandSender sender, String[] args) {
		if(args.length < 2) {
			msg("<b>You must specify the player to inspect");
			return;
		}
		MemeManaPlayerOwner owner = MemeManaPlayerOwner.fromPlayerName(args[1]);
		if(owner == null) {
			msg("<c>%s <b>is not a valid mana owner",args[1]);
			return;
		}
		MemeManaPouch pouch = MemeManaPlugin.getInstance().getManaManager().getPouch(owner);
		double manaAvailable = pouch.getContent();
		msg("<c>%s<i> has <g>%s<i> mana",args[1],String.valueOf(manaAvailable));
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		if(stat.getStreak() != 0) {
			msg("<c>%s<g> is on a <i>%d<g> day login streak",args[1],stat.getStreak());
		}
	}

	public void doGive(CommandSender sender, String[] args) {
		if(args.length < 2) {
			msg("<b>You must specify the player to give mana to");
			return;
		}
		MemeManaOwner owner = MemeManaPlayerOwner.fromPlayerName(args[1]);
		if(owner == null) {
			msg("<c>%s <b>is not a valid mana owner",args[1]);
			return;
		}
		if(args.length < 3) {
			msg("<b>You must specify the amount of mana to give");
			return;
		}
		Integer giveAmount = null;
		try {
			giveAmount = Integer.parseInt(args[2]);
		} catch (Exception e) {
			msg("<i>%s <b>is not a valid amount of mana",args[2]);
			return;
		}
		MemeManaPlugin.getInstance().getManaManager().addMana(owner,giveAmount);
		msg("<g>Gave <c>%s <i>%d<g> mana",args[1],giveAmount);
	}

	public void doReset(CommandSender sender, String[] args) {
		if(args.length < 2) {
			msg("<b>You must specify the player to reset stats for");
			return;
		}
		MemeManaPlayerOwner owner = MemeManaPlayerOwner.fromPlayerName(args[1]);
		if(owner == null) {
			msg("<c>%s <b>is not a valid player",args[1]);
			return;
		}
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		stat.reset();
		MemeManaPlugin.getInstance().getDAO().updateManaStat(owner,stat);
		msg("<g>Reset mana statistics for <c>%s",args[1]);
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
