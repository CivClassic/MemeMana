package com.github.maxopoly.MemeMana.listener;

import com.devotedmc.ExilePearl.event.PlayerPearledEvent;
import com.github.maxopoly.MemeMana.MemeManaOwnerManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PearlListener implements Listener {

	@EventHandler
	public void playerJoin(PlayerPearledEvent e) {
		int oid = MemeManaOwnerManager.fromUUID(e.getPearl().getPlayerId());
		ManaGainStat mgs = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(oid);
		mgs.reset();
		MemeManaPlugin.getInstance().getDAO().updateManaStat(oid,mgs);
	}

}
