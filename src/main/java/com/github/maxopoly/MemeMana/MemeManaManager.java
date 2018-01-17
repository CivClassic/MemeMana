package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
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

	public MemeManaPouch getPouch(MemeManaOwner player) {
		return playerPouches.get(player.selectAlt(playerPouches.keySet()));
	}

	public int getNextManaID() {
		return ++manaCounter;
	}

	//TODO
	// Must keep decay times correct
	// true means successful
	public boolean transferMana(MemeManaOwner from, MemeManaOwner to, double amount) {
		MemeManaPouch fromPouch = getPouch(from);
		MemeManaPouch toPouch = getPouch(to);
		return false;
	}

	public void addMana(MemeManaOwner player, double amount) {
		MemeManaUnit unit = new MemeManaUnit(getNextManaID(),amount);
		getPouch(player).addNewUnit(unit);
		MemeManaPlugin.getInstance().getDAO().addManaUnit(unit,player);
	}
}
