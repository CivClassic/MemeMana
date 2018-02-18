package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;

public class CmdManaSimulateLogin extends PlayerCommand {
	public CmdManaSimulateLogin(String name) {
		super(name);
		setIdentifier("manasimulatelogin");
		setDescription("Simulate logging in for mana purposes");
		setUsage("/manasimulatelogin");
		setArguments(0,0);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			msg("Can't simulate login from console");
			return true;
		}
		Player player = (Player) sender;
		msg("<i>Simulating your login");
		MemeManaPlugin.getInstance().getActivityManager().updatePlayer(player.getUniqueId());
		msg("<i>Millis to next login: " + MemeManaPlugin.getInstance().getActivityManager().getForPlayer(MemeManaOwnerManager.fromPlayer(player)).millisToNextGain());
		
		return true;
	}

	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null; // Defaults to players
	}
}
