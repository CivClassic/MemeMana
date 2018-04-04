package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.PearlType;
import com.devotedmc.ExilePearl.command.BaseCommand;
import com.devotedmc.ExilePearl.util.SpawnUtil;
import com.github.maxopoly.MemeMana.MemeManaDAO;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.Date;
import java.util.function.BiConsumer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;

public class CmdManaUpgrade extends BaseCommand<MemeManaPlugin> {
	private static final MemeManaDAO dao = MemeManaPlugin.getInstance().getDAO();
	public CmdManaUpgrade(MemeManaPlugin mmp) {
		super(mmp);
		this.aliases.add("upgrade");

		this.senderMustBePlayer = true;
		this.setHelpShort("Upgrade a pearl using mana");
	}

	@Override
	public void perform() {
		ItemStack pearlStack = player().getInventory().getItemInMainHand();
		ExilePearl pearl = ExilePearlPlugin.getApi().getPearlFromItemStack(pearlStack);
		if (pearl == null) {
			sender.sendMessage(ChatColor.RED + "You must be holding a pearl to upgrade it");
			return;
		}
		// Ignore pearls that are already upgraded
		if (pearl.getPearlType() == PearlType.PRISON) {
			sender.sendMessage(ChatColor.GREEN + "That pearl is already upgraded!");
			return;
		}
		int pearlUpgradeCost = MemeManaPlugin.getInstance().getManaConfig().getPearlUpgradeAmount();
		int owner = MemeManaOwnerManager.fromPlayer(player());
		MemeManaPouch pouch = MemeManaPouch.getPouch(owner);
		int manaAvailable = pouch.getManaContent();
		if(pearlUpgradeCost > manaAvailable) {
			sender.sendMessage(ChatColor.RED + "Upgrading costs " + ChatColor.GOLD + pearlUpgradeCost + ChatColor.RED + " mana, but you only have " + ChatColor.GOLD + manaAvailable + ChatColor.RED + " mana");
			return;
		}
		long canonTimestamp = new Date().getTime();
		BiConsumer<Long,Integer> logUsage = (l,a) -> {
			dao.logManaUse(dao.getCreatorUUID(pouch.ownerId,l),player().getUniqueId(),pearl.getPlayerId(),a,true,canonTimestamp);
		};
		if(pouch.removeMana(pearlUpgradeCost,logUsage)) {
			pearl.setPearlType(PearlType.PRISON);
			pearl.setHealth(ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthStartValue());
			pearl.validateItemStack(pearlStack);
			sender.sendMessage(ChatColor.GREEN + "The pearl was successfully upgraded");
			if(pearl.getPlayer() != null && pearl.getPlayer().isOnline()) {
				SpawnUtil.spawnPlayer(pearl.getPlayer(), ExilePearlPlugin.getApi().getPearlConfig().getPrisonWorld());
				pearl.getPlayer().sendMessage(ChatColor.YELLOW + "You've been imprisoned in the end by " + player().getDisplayName() + ".");
			}
		}
	}
}
