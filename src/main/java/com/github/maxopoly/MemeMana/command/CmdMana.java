package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.NameAPI;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.PearlType;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import com.github.maxopoly.MemeMana.MemeManaPlayerOwner;
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
		setUsage("\n/mana show -- Show your current mana\n/mana refill [Amount] -- Refill the pearl in your hand by an amount\n/mana transfer <Player> <Amount> -- Transfer some mana to another player");
		setArguments(1,3);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (args[0].equals("show")) {
			showManaAmount(sender);
		} else if (args[0].equals("refill")) {
			doRefill(sender,args);
		} else if (args[0].equals("transfer")) {
			doTransfer(sender,args);
		}
		return true;
	}

	public void showManaAmount(CommandSender sender) {
		if (!(sender instanceof Player)) {
			msg("Can't show your own mana from console. Try /mana inspect instead");
			return;
		}
		MemeManaPouch pouch = MemeManaPlugin.getInstance().getManaManager().getPouch(MemeManaPlayerOwner.fromPlayer((Player)sender));
		double manaAvailable = pouch.getContent();
		msg("<i>You have<g> " + String.valueOf(manaAvailable) + "<i> mana");
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
		int repairPerUnitMana = MemeManaPlugin.getInstance().getManaConfig().getPearlRefillAmount(pearl.getPearlType());
		MemeManaPouch pouch = MemeManaPlugin.getInstance().getManaManager().getPouch(MemeManaPlayerOwner.fromPlayer(player));
		double manaAvailable = pouch.getContent();
		int manaToUse = Math.min((int)Math.ceil((maxHealth - pearl.getHealth()) / (double)repairPerUnitMana), (int)manaAvailable);
		if (pouch.deposit(manaToUse)) {
			pearl.setHealth(Math.min(repairPerUnitMana * manaToUse,maxHealth));
		}
	}

	public void doTransfer(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			msg("Can't transfer mana from console");
			return;
		}
		Player player = (Player) sender;
		if (args.length < 2) {
			msg("<b>Usage: <i>/mana transfer <c>Player");
			return;
		}
		if (MemeManaPlugin.getInstance().getManaManager().transferMana(MemeManaPlayerOwner.fromPlayer(player),MemeManaPlayerOwner.fromPlayerName(args[1]),5)) {
			msg("");
		}
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
