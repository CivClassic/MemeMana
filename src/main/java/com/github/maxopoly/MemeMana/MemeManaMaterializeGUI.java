package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaManager;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Date;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
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
		MemeManaPouch pouch = MemeManaPouch.getPouch(ownerId);
		TreeMap<Long,Double> units = pouch.getRawUnits();
		if (units.size() < 45 * currentPage) {
			// would show an empty page, so go to previous
			currentPage--;
			showScreen();
		}
		if (units.size() == 0) {
			//item to indicate that there is nothing to claim
			ItemStack noClaim = new ItemStack(Material.BARRIER);
			ISUtils.setName(noClaim, ChatColor.GOLD + "No mana available");
			ISUtils.addLore(noClaim, ChatColor.RED + "You currently have no mana");
			ci.setSlot(new DecorationStack(noClaim), 4);
		} else {
			int nextSlot = 0;
			for(long timestamp : units.keySet().stream().skip(45 * currentPage).limit(45).collect(Collectors.toList())){
				ci.setSlot(createManaClickable(pouch,timestamp), nextSlot++);
			};
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
		if ((45 * (currentPage + 1)) <= units.size()) {
			ItemStack forward = new ItemStack(Material.ARROW);
			ISUtils.setName(forward, ChatColor.GOLD + "Go to next page");
			Clickable forCl = new Clickable(forward) {

				@Override
				public void clicked(Player arg0) {
					if ((45 * (currentPage + 1)) <= units.size()) {
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

	private Clickable createManaClickable(MemeManaPouch pouch, long timestamp) {
		// Give them the version without a timestamp or amount indicator
		ItemStack toGive = new ItemStack(Material.EYE_OF_ENDER);
		ISUtils.setName(toGive,"Mana");
		ISUtils.addLore(toGive,"This is a meme mana unit");
		ItemMap toGiveMap = new ItemMap();
		int manaInUnit = (int) pouch.getUnitManaContent(timestamp);
		toGiveMap.addItemAmount(toGive,manaInUnit);
		// Display the version with timestamp and amount indicator
		ItemStack toShow = toGive.clone();
		ISUtils.addLore(toShow,"Generated " + new Date(timestamp).toString());
		ISUtils.addLore(toShow,"Mana: " + manaInUnit);
		return new Clickable(toShow) {
			@Override
			public void clicked(Player p) {
				PlayerInventory pInv = p.getInventory();
				if (toGiveMap.fitsIn(pInv)) {
					pouch.deleteSpecificManaUnitByTimestamp(timestamp);
					toGiveMap.getItemStackRepresentation().forEach(u -> pInv.addItem(u));
				} else {
					p.sendMessage(ChatColor.RED + "There is not enough space in your inventory");
				}
				p.updateInventory();
				showScreen();
			}
		};
	}
}
