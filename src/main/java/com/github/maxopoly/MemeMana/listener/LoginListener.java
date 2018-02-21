package com.github.maxopoly.MemeMana.listener;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Bukkit;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class LoginListener implements Listener {
	private static final Map<UUID,BukkitTask> giveManaTasks = new HashMap<UUID,BukkitTask>();

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		giveManaTasks.put(e.getPlayer().getUniqueId(),Bukkit.getScheduler().runTaskLater(MemeManaPlugin.getInstance(),() -> MemeManaPlugin.getInstance().getActivityManager().updatePlayer(e.getPlayer().getUniqueId()),MemeManaPlugin.getInstance().getManaConfig().getManaWaitTime()));
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		Optional.ofNullable(giveManaTasks.remove(e.getPlayer().getUniqueId())).ifPresent(BukkitTask::cancel);
	}

}
