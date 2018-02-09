package com.github.maxopoly.MemeMana.model;

// Having a MemeManaUnit guaruntees that it exists in the database.
public class MemeManaUnit {
  /*
	private static final MemeManaDAO dao = MemeManaPlugin.getInstance().getDAO();
	public final int ownerId;
	public final long gainTime;

	private MemeManaUnit(int ownerId, long gainTime) {
		this.ownerId = ownerId;
		this.gainTime = gainTime;
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

	public static List<MemeManaUnit> getUnits(int ownerId){
		return getRawUnits().keySet().stream().map(g -> new MemeManaUnit(ownerId,g)).collect(Collectors.toList());
	}

	public static double getManaForOwner(int ownerId){
		return getRawUnits(ownerId).values().stream().mapToDouble(u -> u.getCurrentAmount()).sum();
	}

	public static void reloadFromDB(){
		dao.loadManaPouches(pouchesByOwner);
	}

	public double getManaContent(){
		MemeManaPouch.getPouch(ownerId).get(gainTime);
	}

	// Deletes this mana unit completely and irreversibly
	// You should not attempt to use a deleted mana unit because it will NPE
	public void deleteUnit(){
		dao.snipeManaUnit(ownerId, gainTime);
		pouchesByOwner.get(ownerId).remove(gainTime);
	}
	*/
}
