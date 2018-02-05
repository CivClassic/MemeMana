package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.civclassic.altmanager.AltManager;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaMaterialize extends PlayerCommand {
	public CmdManaMaterialize(String name) {
		super(name);
		setIdentifier("manamaterialize");
		setDescription("Irreversibly materialize your mana into physical items");
		setUsage("/manamaterialize");
		setArguments(0,1);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			msg("Can't refill from console");
			return true;
		}
		MemeManaMaterializeGUI gui = new MemeManaMaterializeGUI(((Player) sender).getUniqueId());
		gui.showScreen();
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
