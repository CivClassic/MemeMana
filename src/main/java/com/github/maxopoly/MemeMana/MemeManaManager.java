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
/*
	private Map<Integer, MemeManaPouch> pouches;

	public MemeManaManager() {
		reloadFromDatabase();
	}

	public static MemeManaUnit createManaUnit(int ownerId, double amount, long gainTime){
		dao.addManaUnit(ownerId, amount, gainTime);
		return new MemeManaUnit(ownerId, gainTime);
	}

	// Gets the mana unit in question or creates an empty one if it doesn't exist
	public static MemeManaUnit getManaUnit(int ownerId, long gainTime){
		pouchesByOwner.putIfAbsent(ownerId, new TreeMap<Long,Double>());
		TreeMap<Long,Double> thisPouch = pouchesByOwner.get(ownerId);
		return thisPouch.containsKey(gainTime) ? new MemeManaUnit(ownerId,gainTime) : createManaUnit(ownerId, 0.0, gainTime);
	}

	// The returned TreeMap backs the cache, so don't modify it
	public static TreeMap<Long,Double> getRawUnits(int ownerId){
		pouchesByOwner.putIfAbsent(ownerId, new TreeMap<Long,Double>());
		return pouchesByOwner.get(ownerId);
	}

	public static List<MemeManaUnit> getUnits(int ownerId){
		return getRawUnits().keySet().stream().map(g -> new MemeManaUnit(ownerId,g)).collect(Collectors.toList());
	}

	public static double getManaForOwner(int ownerId){
		return getRawUnits(ownerId).values().stream().mapToDouble(u -> u.getCurrentAmount()).sum();
	}
*/
}
