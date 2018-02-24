package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.MemeManaDAO;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.function.IntFunction;
import java.util.function.BiConsumer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.civclassic.altmanager.AltManager;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import net.md_5.bungee.api.ChatColor;

public class CmdManaRefill extends PlayerCommand {
	private static final MemeManaOwnerManager ownerManager = MemeManaPlugin.getInstance().getOwnerManager();
	private static final MemeManaDAO dao = MemeManaPlugin.getInstance().getDAO();
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
			sender.sendMessage(ChatColor.RED + "Can't refill from console");
			return true;
		}
		Player player = (Player) sender;
		ExilePearl pearl = ExilePearlPlugin.getApi().getPearlFromItemStack(player.getInventory().getItemInMainHand());
		if (pearl == null) {
			sender.sendMessage(ChatColor.RED + "You must be holding a pearl to refill it");
			return true;
		}
		// Ignore pearls that are at full health
		int maxHealth = ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthMaxValue();
		if (pearl.getHealth() == maxHealth) {
			sender.sendMessage(ChatColor.GREEN + "That pearl is already at max health!");
			return true;
		}
		int repairPerUnitMana = MemeManaPlugin.getInstance().getManaConfig().getPearlRefillAmount(pearl.getPearlType());
		int owner = MemeManaOwnerManager.fromPlayer(player);
		MemeManaPouch pouch = MemeManaPouch.getPouch(owner);
		int manaAvailable = pouch.getManaContent();
		int manaToUse = pouch.getManaContent();
		if (args.length == 1) {
			try {
				manaToUse = Integer.parseInt(args[0]);
				if(manaToUse <= 0){
					throw new NumberFormatException();
				}
			} catch (Exception e) {
				sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid amount of mana");
				return false;
			}
		}
		if(manaToUse > manaAvailable) {
			manaToUse = manaAvailable;
		}
		int healthBefore = pearl.getHealth();
		manaToUse = Math.min((int)Math.ceil((maxHealth - healthBefore) / (double)repairPerUnitMana), manaToUse);
		long canonTimestamp = new Date().getTime();
		BiConsumer<Long,Integer> logUsage = (l,a) -> {
			dao.logManaUse(dao.getCreatorUUID(pouch.ownerId,l),player.getUniqueId(),pearl.getPlayerId(),a,false,canonTimestamp);
		};
		if(pouch.removeMana(manaToUse,logUsage)) {
			pearl.setHealth(Math.min(healthBefore + repairPerUnitMana * manaToUse,maxHealth));
			IntFunction<Integer> toPercent = h -> Math.min(100, Math.max(0, (int)Math.round(((double)h / maxHealth) * 100)));
			sender.sendMessage(ChatColor.GREEN + "The pearl was repaired from " + ChatColor.YELLOW + toPercent.apply(healthBefore) + "%" + ChatColor.GREEN + " health to " + ChatColor.YELLOW + toPercent.apply(pearl.getHealth()) + "%" + ChatColor.GREEN + " health, consuming " + ChatColor.GOLD + manaToUse + " mana");
		}
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
