package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemeManaManager {

	private Map<UUID, MemeManaPouch> playerPouches;
	private int manaCounter;

	public MemeManaManager() {
		playerPouches = new HashMap<UUID, MemeManaPouch>();
		reloadFromDatabase();
	}

	private void reloadFromDatabase() {

	}

	public MemeManaPouch getPouch(UUID player) {
		// TODO altmanager stuff
		return playerPouches.get(player);
	}

	public int getNextManaID() {
		return ++manaCounter;
	}

}
