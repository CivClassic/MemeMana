package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaGUI;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.MemeManaUseLogEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;
import vg.civcraft.mc.namelayer.NameAPI;

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

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Can't view fuel log from console");
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

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
