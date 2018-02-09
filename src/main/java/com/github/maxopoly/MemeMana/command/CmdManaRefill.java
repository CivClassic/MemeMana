package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
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

public class CmdManaRefill extends PlayerCommand {
	private static final MemeManaOwnerManager ownerManager = MemeManaPlugin.getInstance().getOwnerManager();
	public CmdManaRefill(String name) {
		super(name);
		setIdentifier("manarefill");
		setDescription("Refill a pearl using mana");
		setUsage("/manarefill [Amount]");
		setArguments(0,1);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			msg("Can't refill from console");
			return true;
		}
		Player player = (Player) sender;
		ExilePearl pearl = ExilePearlPlugin.getApi().getPearlFromItemStack(player.getInventory().getItemInMainHand());
		if (pearl == null) {
			msg("<b>You must be holding a pearl to refill it");
			return true;
		}
		// Ignore pearls that are at full health
		int maxHealth = ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthMaxValue();
		if (pearl.getHealth() == maxHealth) {
			msg("<g>That pearl is already at max health!");
			return true;
		}
		int repairPerUnitMana = MemeManaPlugin.getInstance().getManaConfig().getPearlRefillAmount(pearl.getPearlType());
		int owner = MemeManaOwnerManager.fromPlayer(player);
		MemeManaPouch pouch = MemeManaPouch.getPouch(owner);
		int manaAvailable = pouch.getManaContent();
		int healthBefore = pearl.getHealth();
		int manaToUse = Math.min((int)Math.ceil((maxHealth - healthBefore) / (double)repairPerUnitMana), manaAvailable);
		if(manaToUse <= 0) {
			msg("<b>You don't have enough mana to refill this pearl at all");
			return true;
		}
		// TODO: This is very very very broken (doesn't actually remove the mana lol)
		if(pouch.removeMana(manaToUse)) {
			pearl.setHealth(Math.min(healthBefore + repairPerUnitMana * manaToUse,maxHealth));
			IntFunction<Integer> toPercent = h -> Math.min(100, Math.max(0, (int)Math.round(((double)h / maxHealth) * 100)));
			msg("<g>The pearl was repaired from <i>%d%%<g> health to <i>%d%%<g> health, consuming <i>%d<g> mana!", toPercent.apply(healthBefore),toPercent.apply(pearl.getHealth()),manaToUse);
		}
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
