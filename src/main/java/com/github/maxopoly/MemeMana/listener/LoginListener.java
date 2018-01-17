package com.github.maxopoly.MemeMana.listener;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.MemeManaIdentity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		MemeManaPlugin.getInstance().getActivityManager().updatePlayer(MemeManaIdentity.fromPlayer(e.getPlayer()));
	}

}
