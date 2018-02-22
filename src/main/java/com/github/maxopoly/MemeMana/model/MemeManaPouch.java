package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.MemeManaConfig;
import com.github.maxopoly.MemeMana.MemeManaDAO;
import java.util.Comparator;
import java.util.Map;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class MemeManaPouch {
	private static final MemeManaConfig config = MemeManaPlugin.getInstance().getManaConfig();
	private static final MemeManaDAO dao = MemeManaPlugin.getInstance().getDAO();
	private static final Map<Integer,MemeManaPouch> allPouchesByOwner = new HashMap<Integer,MemeManaPouch>();
	// TreeMap to ensure chronological ordering by gain time
	private TreeMap<Long,Integer> units;
	public final int ownerId;

	private MemeManaPouch(int ownerId) {
		this(ownerId, new TreeMap<Long,Integer>());
	}

	private MemeManaPouch(int ownerId, TreeMap<Long,Integer> units) {
		this.ownerId = ownerId;
		this.units = units;
	}

	/**
	 * Removes mana past the maximum keep time
	 */
	private void cleanupPouch() {
		long currentTime = System.currentTimeMillis();
		long rotTime = config.getManaRotTime();
		long firstAcceptableTimestamp = currentTime - rotTime;
		units = new TreeMap<Long,Integer>(units.tailMap(firstAcceptableTimestamp));
	}

	public static MemeManaPouch getPouch(int owner) {
		return Optional.ofNullable(allPouchesByOwner.get(owner)).orElseGet(() -> {
			TreeMap<Long,Integer> units = new TreeMap<Long,Integer>();
			dao.loadManaPouch(owner,units);
			MemeManaPouch pouch = new MemeManaPouch(owner,units);
			allPouchesByOwner.put(owner,pouch);
			return pouch;
		});
	}

	public void addMana(int amt, UUID creator) {
		long gainTime = new Date().getTime();
		dao.addManaUnit(amt, ownerId, gainTime, creator);
		units.put(gainTime,amt);
	}

	/**
	 * @return How much mana is currently in this pouch
	 */
	public int getManaContent() {
		cleanupPouch();
		return units.values().stream().mapToInt(e -> e).sum();
	}

	// Returns true if there was enough mana, false if no mana was removed
	public boolean removeMana(int amount) {
		if(getManaContent() < amount){
			return false;
		}
		int leftToRemove = amount;
		Long lastTimestampRemoved = null;
		Integer manaLeftInNextUnit = null;
		Iterator<Long> iter = units.keySet().iterator();
		while(iter.hasNext() && leftToRemove > 0){
			long timestamp = iter.next();
			int manaInThisUnit = units.get(timestamp);
			if(manaInThisUnit <= leftToRemove){
				iter.remove();
				lastTimestampRemoved = timestamp;
				leftToRemove -= manaInThisUnit;
			} else {
				manaLeftInNextUnit = manaInThisUnit - leftToRemove;
				break;
			}
		}
		// If we removed any full units
		if(lastTimestampRemoved != null){
			// Also remove them from the database
			dao.deleteUnitsUntil(ownerId, lastTimestampRemoved);
		}
		// If we partially depleted a unit
		if(manaLeftInNextUnit != null){
			long partialTimestamp = Optional.ofNullable(lastTimestampRemoved).map(t -> units.higherKey(t)).orElse(units.firstKey());
			units.replace(partialTimestamp,manaLeftInNextUnit);
			// Also deplete in the database
			dao.adjustManaUnit(ownerId, partialTimestamp, manaLeftInNextUnit);
		}
		return true;
	}

	public TreeMap<Long,Integer> getRawUnits(){
		return units;
	}

	public int getUnitManaContent(long gainTime){
		return units.get(gainTime);
	}

	public void deleteSpecificManaUnitByTimestamp(long gainTime){
		units.remove(gainTime);
		dao.snipeManaUnit(ownerId, gainTime);
	}

	// Must keep decay times correct
	// true means successful
	public boolean transferMana(MemeManaPouch toPouch, int amount) {
		dao.logManaTransfer(ownerId, toPouch.ownerId, amount);
		if(getManaContent() < amount){
			return false;
		}
		TreeMap<Long,Integer> otherPouchRaw = toPouch.getRawUnits();
		int leftToRemove = amount;
		Long lastTimestampRemoved = null;
		Integer manaLeftInNextUnit = null;
		Iterator<Long> iter = units.keySet().iterator();
		while(iter.hasNext() && leftToRemove > 0){
			long timestamp = iter.next();
			int manaInThisUnit = units.get(timestamp);
			if(manaInThisUnit <= leftToRemove){
				iter.remove();
				otherPouchRaw.merge(timestamp,manaInThisUnit,(a,b) -> a + b);
				lastTimestampRemoved = timestamp;
				leftToRemove -= manaInThisUnit;
			} else {
				manaLeftInNextUnit = manaInThisUnit - leftToRemove;
				otherPouchRaw.merge(timestamp,leftToRemove,(a,b) -> a + b);
				break;
			}
		}
		// If we transfer any full units
		if(lastTimestampRemoved != null){
			// Also transfer them from the database
			dao.transferUnitsUntil(ownerId, toPouch.ownerId, lastTimestampRemoved);
		}
		// If we partially transfer a unit
		if(manaLeftInNextUnit != null){
			long partialTimestamp = Optional.ofNullable(lastTimestampRemoved).map(t -> units.higherKey(t)).orElse(units.firstKey());
			units.replace(partialTimestamp,manaLeftInNextUnit);
			// Adjust the one in our pouch to be the left behind part
			dao.adjustManaUnit(ownerId, partialTimestamp, manaLeftInNextUnit);
			// leftToRemove is now the amount in the other unit
			dao.addManaUnit(leftToRemove, toPouch.ownerId, partialTimestamp, dao.getCreatorUUID(ownerId, partialTimestamp));
			// Making two database accesses is fun :D
		}
		return true;
	}
}
