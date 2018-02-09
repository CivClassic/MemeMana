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
import org.bukkit.inventory.ItemStack;

public class MemeManaPouch {
	private static final MemeManaConfig config = MemeManaPlugin.getInstance().getManaConfig();
	private static final MemeManaDAO dao = MemeManaPlugin.getInstance().getDAO();
	private static final Map<Integer,MemeManaPouch> allPouchesByOwner = new HashMap<Integer,MemeManaPouch>();
	// TreeMap to ensure chronological ordering by gain time
	private TreeMap<Long,Double> units;
	public final int ownerId;

	private MemeManaPouch(int ownerId) {
		this(ownerId, new TreeMap<Long,Double>());
	}

	private MemeManaPouch(int ownerId, TreeMap<Long,Double> units) {
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
		//units.headMap(firstAcceptableTimestamp).keySet().forEach(timestamp -> {
			//dao.snipeManaUnit(ownerId,timestamp);
		//});
		units = new TreeMap(units.tailMap(firstAcceptableTimestamp));
	}

	public static MemeManaPouch getPouch(int owner) {
		return Optional.ofNullable(allPouchesByOwner.get(owner)).orElseGet(() -> {
			TreeMap<Long,Double> units = new TreeMap<Long,Double>();
			dao.loadManaPouch(owner,units);
			MemeManaPouch pouch = new MemeManaPouch(owner,units);
			allPouchesByOwner.put(owner,pouch);
			return pouch;
		});
	}

	public void addMana(double amt) {
		long gainTime = new Date().getTime();
		dao.addManaUnit(amt, ownerId, gainTime);
		units.put(gainTime,amt);
	}

	/**
	 * @return How much mana is currently in this pouch
	 */
	public double getManaContent() {
		cleanupPouch();
		return units.entrySet().stream().mapToDouble(e -> e.getValue() * config.getDecayMultiplier(e.getKey())).sum();
	}

	// Returns true if there was enough mana, false if no mana was removed
	public boolean removeMana(double amount) {
		if(getManaContent() < amount){
			return false;
		}
		double leftToRemove = amount;
		Long lastTimestampRemoved = null;
		Double manaLeftInNextUnit = null;
		Iterator<Long> iter = units.keySet().iterator();
		while(iter.hasNext() && leftToRemove > 0.0001f){
			long timestamp = iter.next();
			double manaInThisUnit = getUnitManaContent(timestamp);
			if(manaInThisUnit <= leftToRemove){
				iter.remove();
				lastTimestampRemoved = timestamp;
				leftToRemove -= manaInThisUnit;
			} else {
				manaLeftInNextUnit = (manaInThisUnit - leftToRemove) / config.getDecayMultiplier(timestamp);
				// Shouldn't be a concurrent modification because we are iterating over the keySet
				units.replace(timestamp,manaLeftInNextUnit);
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
			// Also deplete in the database
			dao.adjustManaUnit(ownerId, units.higherKey(lastTimestampRemoved), manaLeftInNextUnit);
		}
		return true;
	}

	public TreeMap<Long,Double> getRawUnits(){
		return units;
	}

/*
	public List<MemeManaUnit> getUnits(){
		return getRawUnits().keySet().stream().map(g -> new MemeManaUnit(ownerId,g)).collect(Collectors.toList());
	}
*/
	public double getUnitManaContent(long gainTime){
		return units.get(gainTime) * config.getDecayMultiplier(gainTime);
	}

	public void deleteSpecificManaUnitByTimestamp(long gainTime){
		units.remove(gainTime);
		dao.snipeManaUnit(ownerId, gainTime);
	}

	// Must keep decay times correct
	// true means successful
	public boolean transferMana(MemeManaPouch toPouch, double amount) {
	if(getManaContent() < amount){
			return false;
		}
		TreeMap<Long,Double> otherPouchRaw = toPouch.getRawUnits();
		double leftToRemove = amount;
		Long lastTimestampRemoved = null;
		Double manaLeftInNextUnit = null;
		Iterator<Long> iter = units.keySet().iterator();
		while(iter.hasNext() && leftToRemove > 0.0001f){
			long timestamp = iter.next();
			double decayMult = config.getDecayMultiplier(timestamp);
			// More gets removed from older units because they are decayed more
			double toRemoveTimeDeprecated = leftToRemove / decayMult;
			double manaInThisUnit = units.get(timestamp);
			if(manaInThisUnit <= toRemoveTimeDeprecated){
				iter.remove();
				otherPouchRaw.merge(timestamp,manaInThisUnit,(a,b) -> a + b);
				lastTimestampRemoved = timestamp;
				leftToRemove -= manaInThisUnit * decayMult;
			} else {
				manaLeftInNextUnit = manaInThisUnit - toRemoveTimeDeprecated;
				otherPouchRaw.merge(timestamp,toRemoveTimeDeprecated,(a,b) -> a + b);
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
			// leftToRemove is now the non-time-adjusted-amount in the other unit
			dao.addManaUnit(leftToRemove / config.getDecayMultiplier(partialTimestamp), toPouch.ownerId, partialTimestamp);
		}
		return true;
	}
}
