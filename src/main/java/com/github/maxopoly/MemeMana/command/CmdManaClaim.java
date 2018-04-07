package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.listener.LoginListener;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaClaim extends PlayerCommand {
	public CmdManaClaim(String name) {
		super(name);
		setIdentifier("manaclaim");
		setDescription("Claim your mana for logging in");
		setUsage("/manaclaim");
		setArguments(0,0);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't claim mana from console.");
			return true;
		}
		Player p = (Player) sender;
		Long recentLogin = LoginListener.manaLoginTimes.get(p.getUniqueId());
		if(recentLogin == null){
			sender.sendMessage(ChatColor.RED + "Failed to determine when you logged in.");
			return true;
		}
		long timeRemaining = (MemeManaPlugin.getInstance().getManaConfig().getManaWaitTime() * 50L) - (System.currentTimeMillis() - recentLogin);
		
		if(timeRemaining <= 0L || 1 == (1 & MemeManaPlugin.getInstance().getActivityManager().getForPlayer(MemeManaOwnerManager.fromPlayer(p)).getStreakField())){
			MemeManaPlugin.getInstance().getActivityManager().updatePlayer(p.getUniqueId());
		}else{
			sender.sendMessage(ChatColor.RED + "Please wait " + (timeRemaining / 60000L) + " minutes before claiming your mana.");
		}
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
