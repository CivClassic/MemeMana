package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.owners.MemeManaOwner;
import java.util.Map;
import java.util.TreeMap;

public class MemeManaOwnerManager {

	private Map <Integer, Map<Integer, MemeManaOwner>> ownersByTypeAndExternal;
	private Map <Integer, MemeManaOwner> ownersByInternal;
	private int nextManaId;

	public MemeManaOwnerManager() {
		this.ownersByTypeAndExternal = new TreeMap<Integer, Map<Integer,MemeManaOwner>>();
		this.ownersByInternal = new TreeMap<Integer, MemeManaOwner>();
		reloadFromDatabase();
	}


	private void reloadFromDatabase() {
		for(MemeManaOwner owner : MemeManaPlugin.getInstance().getDAO().loadAllMana().values()) {
			register(owner);
		}
		this.nextManaId = MemeManaPlugin.getInstance().getDAO().getNextManaId();
	}

	public void register(MemeManaOwner owner) {
		Map <Integer, MemeManaOwner> typedOwners = ownersByTypeAndExternal.get(owner.getOwnerType());
		if (typedOwners == null) {
			typedOwners = new TreeMap<Integer, MemeManaOwner>();
			ownersByTypeAndExternal.put(owner.getOwnerType(), typedOwners);
		}
		typedOwners.put(owner.getForeignId(), owner);
		ownersByInternal.put(owner.getID(), owner);
	}

	public MemeManaOwner getOwnerByExternal(int ownerType, int externalId) {
		Map <Integer, MemeManaOwner> typedOwners = ownersByTypeAndExternal.get(ownerType);
		if (typedOwners == null) {
			return null;
		}
		return typedOwners.get(externalId);
	}

	public MemeManaOwner getOwnerByInternal(int internalId) {
		return ownersByInternal.get(internalId);
	}
}
