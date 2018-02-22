package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.civclassic.altmanager.AltManager;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import net.md_5.bungee.api.ChatColor;

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
			sender.sendMessage(ChatColor.RED + "Can't materialize from console");
			return true;
		}
		UUID player = ((Player) sender).getUniqueId();
		MemeManaMaterializeGUI gui = new MemeManaMaterializeGUI(player,player,true);
		gui.showScreen();
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
