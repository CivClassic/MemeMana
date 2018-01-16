package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import java.util.Set;
import java.util.UUID;
import com.programmerdan.minecraft.banstick.data.BSPlayer;

public class MemeManaIdentity implements MemeManaOwner {
	private long bsId;

	public MemeManaIdentity(UUID uuid) {
		this.bsId = BSPlayer.byUUID(uuid).getId();
	}

	public MemeManaIdentity(long bsId) {
		this.bsId = bsId;
	}

	public long getID() {
		return bsId;
	}

	public long selectAlt(Set<Long> theSet) {
		Long found = null;
		for(long u : theSet) {
			if(this.associatedWith(u)) {
				if(found == null) {
					found = u;
				} else {
					theSet.remove(u);
				}
			}
		}
		return found;
	}

	public boolean associatedWith(long other) {
		return BSPlayer.byId(bsId).getUnpardonedShares().stream().anyMatch(s ->
			(s.getFirstPlayer().getId() == bsId && s.getSecondPlayer().getId() == other) ||
			(s.getFirstPlayer().getId() == other && s.getSecondPlayer().getId() == bsId));
	}


}
