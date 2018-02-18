package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.PearlType;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.civclassic.altmanager.AltManager;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import net.md_5.bungee.api.ChatColor;

public class CmdManaUpgrade extends PlayerCommand {
	private static final MemeManaOwnerManager ownerManager = MemeManaPlugin.getInstance().getOwnerManager();
	public CmdManaUpgrade(String name) {
		super(name);
		setIdentifier("manaupgrade");
		setDescription("Upgrade a pearl using mana");
		setUsage("/manaupgrade");
		setArguments(0,0);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't refill from console");
			return true;
		}
		Player player = (Player) sender;
		ExilePearl pearl = ExilePearlPlugin.getApi().getPearlFromItemStack(player.getInventory().getItemInMainHand());
		if (pearl == null) {
			sender.sendMessage(ChatColor.RED + "You must be holding a pearl to upgrade it");
			return true;
		}
		// Ignore pearls that are already upgraded
		int maxHealth = ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthMaxValue();
		if (pearl.getPearlType() == PearlType.PRISON) {
			sender.sendMessage(ChatColor.GREEN + "That pearl is already upgraded!");
			return true;
		}
		int pearlUpgradeCost = MemeManaPlugin.getInstance().getManaConfig().getPearlUpgradeAmount();
		int owner = MemeManaOwnerManager.fromPlayer(player);
		MemeManaPouch pouch = MemeManaPouch.getPouch(owner);
		int manaAvailable = pouch.getManaContent();
		if(pearlUpgradeCost > manaAvailable) {
			sender.sendMessage(ChatColor.RED + "Upgrading costs " + ChatColor.GOLD + pearlUpgradeCost + ChatColor.RED + " mana, but you only have " + ChatColor.GOLD + manaAvailable + ChatColor.RED + " mana");
			return true;
		}
		if(pouch.removeMana(pearlUpgradeCost)) {
			pearl.setPearlType(PearlType.PRISON);
			pearl.setHealth(ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthStartValue());
			sender.sendMessage(ChatColor.GREEN + "The pearl was successfully upgraded");
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<String>(); //empty list
	}
}
