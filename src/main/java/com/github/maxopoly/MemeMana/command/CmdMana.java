package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.NameAPI;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.ExilePearl;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.UUID;
import java.util.List;
import java.util.LinkedList;

public class CmdMana extends PlayerCommand {
	public CmdMana(String name) {
		super(name);
		setIdentifier("mana");
		setDescription("The base command for Meme Mana");
		setUsage("/mana");
		setArguments(0, 1);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (args.length == 0 || args[0] == "help") {
			showHelpText(sender,args);
		} else if (args[0] == "refill") {
			doRefill(sender,args);
		} else if (args[0] == "transfer") {
			doTransfer(sender,args);
		} else if (args[0] == "transfer") {
			doRefill(sender,args);
		}
		return true;
	}

	public void showHelpText(CommandSender sender, String[] args) {
	//TODO
	}
	public void doRefill(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			msg("Can't refill from console");
			return;
		}
		Player player = (Player) sender;
		ExilePearl pearl = ExilePearlPlugin.getApi().getPearlFromItemStack(player.getInventory().getItemInMainHand());
		if (pearl == null) {
			msg("You need to hold a pearl");
			return;
		}
		// Ignore pearls that are at full health
		int maxHealth = ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthMaxValue();
		if (pearl.getHealth() == maxHealth) {
			msg("<g>That pearl is already at max health!");
			return;
		}
		int repairPerUnitMana = 5;
		MemeManaPouch pouch = MemeManaPlugin.getInstance().getManaManager().getPouch(player.getUniqueId());
		int manaAvailable = pouch.getContent();
		int manaToUse = Math.min((int)Math.ceil((maxHealth - pearl.getHealth()) / (double)repairPerUnitMana), manaAvailable);
		if (pouch.deposit(manaToUse)) {
			pearl.setHealth(repairPerUnitMana * manaToUse);
		}
	}

	public void doTransfer(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			msg("Can't transfer from console");
			return;
		}
		Player player = (Player) sender;
		if (args.length < 2) {
			msg("Must specify player to transfer to");
			return;
		}
		UUID toUUID = NameAPI.getUUID(args[1]);
		if (MemeManaPlugin.getInstance().getManaManager().transferMana(player.getUniqueId(),toUUID,5)) {
			msg("Transfer worked");
		}
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
