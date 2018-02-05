package com.github.maxopoly.MemeMana;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Stream;
import vg.civcraft.mc.namelayer.NameAPI;
import com.civclassic.altmanager.AltManager;
import org.bukkit.entity.Player;

import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;

public class MemeManaManager {
	private Map <Integer, MemeManaPouch> pouches;
	private int nextManaId;

	public MemeManaManager() {
		reloadFromDatabase();
	}

	private void reloadFromDatabase() {
		this.pouches = MemeManaPlugin.getInstance().getDAO().loadManaPouches();
		this.nextManaId = MemeManaPlugin.getInstance().getDAO().getNextManaId();
	}

	public MemeManaPouch getPouch(int internalId) {
		pouches.putIfAbsent(internalId,new MemeManaPouch());
		return pouches.get(internalId);
	}

	//TODO
	// Must keep decay times correct
	// true means successful
	public boolean transferMana(Integer from, Integer to, double amount) {
		MemeManaPouch fromPouch = getPouch(from);
		MemeManaPouch toPouch = getPouch(to);
		return false;
	}

	public void addMana(Integer player, double amount) {
		MemeManaUnit unit = new MemeManaUnit(getNextManaID(),amount);
		getPouch(player).addNewUnit(unit);
		MemeManaPlugin.getInstance().getDAO().addManaUnit(unit,player);
	}

	public int getNextManaID() {
		return nextManaId++;
	}

	public void removeManaUnitById(int ownerId, int manaId) {
		getPouch(ownerId).removeUnitById(manaId);
		MemeManaPlugin.getInstance().getDAO().snipeManaUnit(manaId);
	}
}
