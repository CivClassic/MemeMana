package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaGUI;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.MemeManaTransferLogEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;

public class CmdManaTransactionLog extends PlayerCommand {
	private static final SimpleDateFormat manaDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
	static{
		manaDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	public CmdManaTransactionLog(String name) {
		super(name);
		setIdentifier("manaviewtranslog");
		setDescription("See transactions for a mana owner");
		setUsage("/manaviewtranslog");
		setArguments(1,1);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't view transaction log from console");
			return true;
		}
		Integer target = MemeManaOwnerManager.fromName(args[0]);
		if (target == null) {
			sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid mana owner");
			return false;
		}
		MemeManaGUI<MemeManaTransferLogEntry> gui = new MemeManaGUI<MemeManaTransferLogEntry>(((Player) sender).getUniqueId(),() -> MemeManaPlugin.getInstance().getDAO().getTransferLog(target).collect(Collectors.toList()),use -> {
			ItemStack toShow = new ItemStack(Material.DOUBLE_PLANT);
			ISUtils.setName(toShow,"Mana Transfer");
			ISUtils.addLore(toShow,"From Mana Id: " + use.from);
			ISUtils.addLore(toShow,"To Mana Id: " + use.to);
			ISUtils.addLore(toShow,"Mana: " + use.manaAmount);
			ISUtils.addLore(toShow,manaDateFormat.format(new Date(use.timestamp)));
			return toShow;
		},(timestamp,p) -> {});
		gui.showScreen();
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
