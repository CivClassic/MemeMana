package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.command.BaseCommand;
import com.github.maxopoly.MemeMana.MemeManaDAO;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;

public class CmdManaRefill extends BaseCommand<MemeManaPlugin> {
	private static final MemeManaDAO dao = MemeManaPlugin.getInstance().getDAO();
	public CmdManaRefill(MemeManaPlugin mmp) {
		super(mmp);
		this.aliases.add("refill");

		this.senderMustBePlayer = true;
		this.commandArgs.add(optional("amount"));
		this.setHelpShort("Refill a pearl using mana");
	}

	@Override
	public void perform() {
		ItemStack pearlStack = player().getInventory().getItemInMainHand();
		ExilePearl pearl = ExilePearlPlugin.getApi().getPearlFromItemStack(pearlStack);
		if (pearl == null) {
			sender.sendMessage(ChatColor.RED + "You must be holding a pearl to refill it");
			return;
		}
		// Ignore pearls that are at full health
		int maxHealth = ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthMaxValue();
		if (pearl.getHealth() == maxHealth) {
			sender.sendMessage(ChatColor.GREEN + "That pearl is already at max health!");
			return;
		}
		int repairPerUnitMana = MemeManaPlugin.getInstance().getManaConfig().getPearlRefillAmount(pearl.getPearlType());
		int owner = MemeManaOwnerManager.fromPlayer(player());
		MemeManaPouch pouch = MemeManaPouch.getPouch(owner);
		int manaAvailable = pouch.getManaContent();
		int manaToUse = pouch.getManaContent();
		if (args.size() == 1) {
			try {
				manaToUse = Integer.parseInt(args.get(0));
				if(manaToUse <= 0){
					throw new NumberFormatException();
				}
			} catch (Exception e) {
				sender.sendMessage(ChatColor.DARK_RED + args.get(0) + ChatColor.RED + " is not a valid amount of mana");
				return;
			}
		}
		if(manaToUse > manaAvailable) {
			manaToUse = manaAvailable;
		}
		int healthBefore = pearl.getHealth();
		manaToUse = Math.min((int)Math.ceil((maxHealth - healthBefore) / (double)repairPerUnitMana), manaToUse);
		long canonTimestamp = new Date().getTime();
		BiConsumer<Long,Integer> logUsage = (l,a) -> {
			dao.logManaUse(dao.getCreatorUUID(pouch.ownerId,l),player().getUniqueId(),pearl.getPlayerId(),a,false,canonTimestamp);
		};
		if(pouch.removeMana(manaToUse,logUsage)) {
			pearl.setHealth(Math.min(healthBefore + repairPerUnitMana * manaToUse,maxHealth));
			pearl.validateItemStack(pearlStack);
			IntFunction<Integer> toPercent = h -> Math.min(100, Math.max(0, (int)Math.round(((double)h / maxHealth) * 100)));
			sender.sendMessage(ChatColor.GREEN + "The pearl was repaired from " + ChatColor.YELLOW + toPercent.apply(healthBefore) + "%" + ChatColor.GREEN + " health to " + ChatColor.YELLOW + toPercent.apply(pearl.getHealth()) + "%" + ChatColor.GREEN + " health, consuming " + ChatColor.GOLD + manaToUse + " mana");
		}
	}
}
