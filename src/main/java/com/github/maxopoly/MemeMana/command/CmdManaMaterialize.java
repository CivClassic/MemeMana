package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaGUI;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;

public class CmdManaMaterialize extends PlayerCommand {
	public CmdManaMaterialize(String name) {
		super(name);
		setIdentifier("manamaterialize");
		setDescription("Irreversibly materialize your mana into physical items");
		setUsage("/manamaterialize");
		setArguments(0,1);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't materialize from console");
			return true;
		}
		UUID player = ((Player) sender).getUniqueId();
		MemeManaPouch pouch = MemeManaPouch.getPouch(MemeManaOwnerManager.fromUUID(player));
		MemeManaGUI<Long> gui = new MemeManaGUI<Long>(player,() -> pouch.getRawUnits().keySet(),pouch::getDisplayStack,(timestamp,p) -> {
			PlayerInventory pInv = p.getInventory();
			ItemMap toGiveMap = pouch.getPhysicalMana(timestamp);
			if (toGiveMap.fitsIn(pInv)) {
				int manaContent = pouch.getUnitManaContent(timestamp);
				if(manaContent == 1){
					pouch.deleteSpecificManaUnitByTimestamp(timestamp);
				}else{
					pouch.adjustSpecificManaUnitByTimestamp(timestamp, manaContent - 1);
				}
				toGiveMap.getItemStackRepresentation().forEach(u -> pInv.addItem(u));
			} else {
				p.sendMessage(ChatColor.RED + "There is not enough space in your inventory");
			}
			p.updateInventory();
		});
		gui.showScreen();
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
