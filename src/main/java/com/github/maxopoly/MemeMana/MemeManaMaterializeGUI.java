package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaManager;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Material;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;

public class MemeManaMaterializeGUI {
	private static final MemeManaOwnerManager ownerManager = MemeManaPlugin.getInstance().getOwnerManager();
	private static final MemeManaManager manaManager = MemeManaPlugin.getInstance().getManaManager();
	private final UUID uuid;
	private int currentPage;

	public MemeManaMaterializeGUI(UUID uuid){
		this.uuid = uuid;
		this.currentPage = 0;
	}

	public void showScreen() {
		Player p = Bukkit.getPlayer(uuid);
		if(p == null){
			return;
		}
		ClickableInventory.forceCloseInventory(p);
		ClickableInventory ci = new ClickableInventory(54, "Mana inventory");
		int ownerId = MemeManaOwnerManager.fromUUID(uuid);
		MemeManaPouch pouch = MemeManaPlugin.getInstance().getManaManager().getPouch(ownerId);
		List<MemeManaUnit> rawUnits = pouch.getRawUnits();
		if (rawUnits.size() < 45 * currentPage) {
			// would show an empty page, so go to previous
			currentPage--;
			showScreen();
		}
		if (rawUnits.size() == 0) {
			//item to indicate that there is nothing to claim
			ItemStack noClaim = new ItemStack(Material.BARRIER);
			ISUtils.setName(noClaim, ChatColor.GOLD + "No mana available");
			ISUtils.addLore(noClaim, ChatColor.RED + "You currently have no mana");
			ci.setSlot(new DecorationStack(noClaim), 4);
		} else {
			for (int i = 45 * currentPage; i < 45 * (currentPage + 1) && i < rawUnits.size(); i++) {
				ci.setSlot(createManaClickable(ownerId,rawUnits.get(i)), i - (45 * currentPage));
			}
		}
		// previous button
		if (currentPage > 0) {
			ItemStack back = new ItemStack(Material.ARROW);
			ISUtils.setName(back, ChatColor.GOLD + "Go to previous page");
			Clickable baCl = new Clickable(back) {

				@Override
				public void clicked(Player arg0) {
					if (currentPage > 0) {
						currentPage--;
					}
					showScreen();
				}
			};
			ci.setSlot(baCl, 45);
		}
		// next button
		if ((45 * (currentPage + 1)) <= rawUnits.size()) {
			ItemStack forward = new ItemStack(Material.ARROW);
			ISUtils.setName(forward, ChatColor.GOLD + "Go to next page");
			Clickable forCl = new Clickable(forward) {

				@Override
				public void clicked(Player arg0) {
					if ((45 * (currentPage + 1)) <= rawUnits.size()) {
						currentPage++;
					}
					showScreen();
				}
			};
			ci.setSlot(forCl, 53);
		}
		// exit button
		ItemStack backToOverview = new ItemStack(Material.WOOD_DOOR);
		ISUtils.setName(backToOverview, ChatColor.GOLD + "Close");
		ci.setSlot(new Clickable(backToOverview) {

			@Override
			public void clicked(Player arg0) {
				// just let it close, dont do anything
			}
		}, 49);

		ci.showInventory(p);
	}

	private Clickable createManaClickable(int ownerId,MemeManaUnit unit) {
		ItemStack isRepr = unit.getItemStackRepr(false);
		return new Clickable(unit.getItemStackRepr(true)) {
			@Override
			public void clicked(Player p) {
				PlayerInventory pInv = p.getInventory();
				if (new ItemMap(isRepr).fitsIn(pInv)) {
					manaManager.removeManaUnitById(ownerId,unit.getID());
					pInv.addItem(isRepr);
				} else {
					p.sendMessage(ChatColor.RED + "There is not enough space in your inventory");
				}
				p.updateInventory();
				showScreen();
			}
		};
	}
}
