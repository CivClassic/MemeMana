package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.owners.MemeManaOwner;
import com.github.maxopoly.MemeMana.model.owners.MemeManaPlayerOwner;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdMana extends PlayerCommand {
	public CmdMana(String name) {
		super(name);
		setIdentifier("mana");
		setDescription("The base command for Meme Mana");
		setUsage("\n/mana show -- Show your current mana\n/mana refill [Amount] -- Refill the pearl in your hand by an amount\n/mana transfer <Player> <Amount> -- Transfer some mana to another player");
		setArguments(1,3);
	}

	@Override
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
			msg("Can't show your own mana from console.");
			return;
		}
		MemeManaPlayerOwner owner = MemeManaPlayerOwner.fromPlayer((Player)sender);
		MemeManaPouch pouch = owner.getPouch();
		double manaAvailable = pouch.getContent();
		msg("<i>You have<g> %s<i> mana",String.valueOf(manaAvailable));
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		if(stat.getStreak() != 0) {
			msg("<g>You are on a <i>%d<g> day login streak",stat.getStreak());
		}
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
		int healthBefore = pearl.getHealth();
		int manaToUse = Math.min((int)Math.ceil((maxHealth - healthBefore) / (double)repairPerUnitMana), (int)manaAvailable);
		if(manaToUse <= 0) {
			msg("<b>You don't have enough mana to refill this pearl at all");
			return;
		}
		if(pouch.deposit(manaToUse)) {
			pearl.setHealth(Math.min(healthBefore + repairPerUnitMana * manaToUse,maxHealth));
			IntFunction<Integer> toPercent = h -> Math.min(100, Math.max(0, (int)Math.round(((double)h / maxHealth) * 100)));
			msg("<g>The pearl was repaired from <i>%d%%<g> health to <i>%d%%<g> health, consuming <i>%d<g> mana!", toPercent.apply(healthBefore),toPercent.apply(pearl.getHealth()),manaToUse);
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
		MemeManaOwner transferTo = MemeManaPlayerOwner.fromPlayerName(args[1]);
		if (transferTo == null) {
			msg("<c>%s <b>is not a valid player",args[1]);
			return;
		}

		int transferAmount = (int) MemeManaPlugin.getInstance().getManaManager().getPouch(MemeManaPlayerOwner.fromPlayer(player)).getContent();
		if (args.length == 3) {
			try {
				transferAmount = Integer.parseInt(args[2]);
			} catch (Exception e) {
				msg("<i>%s <b>is not a valid amount of mana",args[2]);
				return;
			}
		}
		//if (MemeManaPlugin.getInstance().getManaManager().transferMana(MemeManaPlayerOwner.fromPlayer(player),transferTo,transferAmount)) {}
		msg("Sorry, mana transfer is not implemented yet");
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
