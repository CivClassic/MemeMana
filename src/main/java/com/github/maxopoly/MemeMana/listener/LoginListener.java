package com.github.maxopoly.MemeMana.listener;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class LoginListener implements Listener {
	public static final Map<UUID,Long> manaLoginTimes = new HashMap<UUID,Long>();
	private static final Map<UUID,BukkitTask> notifyTasks = new HashMap<UUID,BukkitTask>();

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		manaLoginTimes.put(e.getPlayer().getUniqueId(),System.currentTimeMillis());
		notifyTasks.put(e.getPlayer().getUniqueId(),Bukkit.getScheduler().runTaskLater(MemeManaPlugin.getInstance(),() -> com.civclassic.altmanager.AltManager.instance().getAlts(e.getPlayer().getUniqueId()).forEach(u -> Optional.ofNullable(Bukkit.getPlayer(u)).ifPresent(p -> p.sendMessage(ChatColor.GREEN + "Your mana is ready to claim. Use " + ChatColor.YELLOW + "/manaclaim" + ChatColor.GREEN + " to receive it"))),MemeManaPlugin.getInstance().getManaConfig().getManaWaitTime()));
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		manaLoginTimes.remove(e.getPlayer().getUniqueId());
		notifyTasks.remove(e.getPlayer().getUniqueId());
	}
}
