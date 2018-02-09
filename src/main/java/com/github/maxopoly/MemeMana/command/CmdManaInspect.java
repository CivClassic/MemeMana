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

public class CmdManaInspect extends PlayerCommand {
	private static final DecimalFormat manaFormat = new DecimalFormat("####.###");
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
			msg("<c>%s <b>is not a valid mana owner",args[0]);
			return false;
		}
		double manaAvailable = MemeManaPouch.getPouch(owner).getManaContent();
		msg("<c>%s<i> has <g>%s<i> mana",args[0],manaFormat.format(manaAvailable));
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		if(stat.getStreak() != 0) {
			msg("<c>%s<g> is on a <i>%d<g> day login streak",args[0],stat.getStreak());
		}
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
