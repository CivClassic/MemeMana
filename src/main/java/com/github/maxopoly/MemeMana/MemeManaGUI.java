package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Date;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.BiConsumer;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
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
import vg.civcraft.mc.namelayer.NameAPI;

public class MemeManaGUI<T> {
	private static final MemeManaOwnerManager ownerManager = MemeManaPlugin.getInstance().getOwnerManager();
	private static final SimpleDateFormat manaDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
	static{
		manaDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	private final UUID showUUID;
	private final Supplier<Collection<T>> getDisplayItems;
	private final Function<T,ItemStack> getDisplayStack;
	private final BiConsumer<T,Player> onClick;
	private int currentPage;

	public MemeManaGUI(UUID showPlayer, Supplier<Collection<T>> getDisplayItems, Function<T,ItemStack> getDisplayStack, BiConsumer<T,Player> onClick){
		this.showUUID = showPlayer;
		this.getDisplayItems = getDisplayItems;
		this.getDisplayStack = getDisplayStack;
		this.onClick = onClick;
		this.currentPage = 0;
	}

	public void showScreen() {
		Player p = Bukkit.getPlayer(showUUID);
		if(p == null){
			return;
		}
		ClickableInventory.forceCloseInventory(p);
		ClickableInventory ci = new ClickableInventory(54, "Mana inventory");
		Collection<T> units = getDisplayItems.get();
		if (units.size() < 45 * currentPage) {
			// would show an empty page, so go to previous
			currentPage--;
			showScreen();
		}
		if (units.size() == 0) {
			//item to indicate that there is nothing to claim
			ItemStack noClaim = new ItemStack(Material.BARRIER);
			ISUtils.setName(noClaim, ChatColor.GOLD + "Nothing here");
			ci.setSlot(new DecorationStack(noClaim), 4);
		} else {
			int nextSlot = 0;
			for(T timestamp : units.stream().skip(45 * currentPage).limit(45).collect(Collectors.toList())){
				ci.setSlot(new Clickable(getDisplayStack.apply(timestamp)) {
						@Override
						public void clicked(Player p) {
							onClick.accept(timestamp,p);
							showScreen();
						}
					},nextSlot++);
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
			public void clicked(Player player) {
				player.closeInventory();
			}
		}, 49);

		ci.showInventory(p);
	}
}
