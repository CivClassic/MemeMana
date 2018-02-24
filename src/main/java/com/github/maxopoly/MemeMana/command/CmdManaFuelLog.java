package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.NameAPI;
import com.github.maxopoly.MemeMana.model.MemeManaUseLogEntry;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import java.util.UUID;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;
import net.md_5.bungee.api.ChatColor;

public class CmdManaFuelLog extends PlayerCommand {
	private static final SimpleDateFormat manaDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
	static{
		manaDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	public CmdManaFuelLog(String name) {
		super(name);
		setIdentifier("manafuellog");
		setDescription("See who has been keeping you pearled");
		setUsage("/manafuellog");
		setArguments(0,0);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't materialize from console");
			return true;
		}
		UUID playerId = ((Player) sender).getUniqueId();
		MemeManaGUI<MemeManaUseLogEntry> gui = new MemeManaGUI<MemeManaUseLogEntry>(playerId,() -> MemeManaPlugin.getInstance().getDAO().getUseLog(playerId).collect(Collectors.toList()),use -> {
			ItemStack toShow = new ItemStack(Material.EYE_OF_ENDER);
			ISUtils.setName(toShow,NameAPI.getCurrentName(use.creator));
			if(use.isUpgrade){
				ISUtils.addLore(toShow,"Upgraded by: " + NameAPI.getCurrentName(use.fueler));
			}else{
				ISUtils.addLore(toShow,"Refueled by: " + NameAPI.getCurrentName(use.fueler));
			}
			ISUtils.addLore(toShow,"Mana: " + use.manaAmount);
			ISUtils.addLore(toShow,manaDateFormat.format(new Date(use.timestamp)));
			return toShow;
		},(timestamp,p) -> {});
		gui.showScreen();
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
