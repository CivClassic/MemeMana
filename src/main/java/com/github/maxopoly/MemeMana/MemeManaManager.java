package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemeManaManager {

	private Map<Long, MemeManaPouch> playerPouches;
	private int nextManaId;

	public MemeManaManager() {
		playerPouches = new HashMap<Long, MemeManaPouch>();
		reloadFromDatabase();
	}

	private void reloadFromDatabase() {
		this.playerPouches = MemeManaPlugin.getInstance().getDAO().getManaPouches();
		nextManaId = 0;
		playerPouches.forEach((k,v) -> {
			for(MemeManaUnit u : v.getUnits()) {
				if(u.getID() >= nextManaId) {nextManaId = u.getID() + 1;}
			}
		});
	}

	public MemeManaPouch getPouch(MemeManaOwner player) {
		Long pid = player.selectAlt(playerPouches.keySet());
		if(pid != null) {
			return playerPouches.get(pid);
		} else {
			MemeManaPouch pouch = new MemeManaPouch();
			playerPouches.put(player.getID(),pouch);
			return pouch;
		}
	}

	public int getNextManaID() {
		return nextManaId++;
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
