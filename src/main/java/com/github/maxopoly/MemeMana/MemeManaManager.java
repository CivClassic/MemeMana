package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemeManaManager {

	private Map<Integer, MemeManaPouch> pouches;
	private int nextManaId;

	public MemeManaManager() {
		reloadFromDatabase();
	}

	private void reloadFromDatabase() {
		this.pouches = MemeManaPlugin.getInstance().getDAO().getManaPouches();
		this.nextManaId = MemeManaPlugin.getInstance().getDAO().getNextManaId();
	}

	public MemeManaPouch getPouch(MemeManaOwner owner) {
		pouches.putIfAbsent(owner.getID(),new MemeManaPouch());
		return pouches.get(owner.getID());
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
