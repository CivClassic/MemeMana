package com.github.maxopoly.MemeMana.model;

import java.util.Set;

public interface MemeManaOwner {

	// The BanStick player ID of this owner
	public long getID();

	// True if this owner is associated with (an alt of) the other owner
	public boolean associatedWith(MemeManaOwner other);

	// Select an alt from the given set which is an alt of this owner
	// while removing all other alts of this owner from the set
	public long selectAlt(Set<Long> theSet);
}
