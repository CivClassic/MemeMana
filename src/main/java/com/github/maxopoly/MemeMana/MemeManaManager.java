package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.programmerdan.minecraft.banstick.data.BSPlayer;

public class MemeManaManager {

	private Map<Long, MemeManaPouch> playerPouches;
	private int manaCounter;

	public MemeManaManager() {
		playerPouches = new HashMap<Long, MemeManaPouch>();
		reloadFromDatabase();
	}

	private void reloadFromDatabase() {
		this.playerPouches = MemeManaPlugin.getInstance().getDAO().getManaPouches();
	}

	public MemeManaPouch getPouch(UUID player) {
		return playerPouches.get(new MemeManaIdentity(player).selectAlt(playerPouches.keySet()));
	}

	public int getNextManaID() {
		return ++manaCounter;
	}

	//TODO
  // Must keep decay times correct
  // true means successful
	public boolean transferMana(UUID from, UUID to, double amount) {
		MemeManaPouch fromPouch = getPouch(from);
		MemeManaPouch toPouch = getPouch(to);
		return false;
	}
}
