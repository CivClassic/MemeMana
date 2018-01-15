package com.github.maxopoly.MemeMana;

import java.util.UUID;
import java.util.Set;
import com.programmerdan.minecraft.banstick.data.BSPlayer;

public class MemeManaIdentity {
	public static UUID selectAlt(Set<UUID> theSet, UUID player) {
		UUID found = null;
		for(UUID u : theSet) {
			if(associatedWith(player,u)) {
				if(found == null) {
					found = u;
				} else {
					theSet.remove(u);
				}
			}
		}
		return found;
	}

	private static boolean associatedWith(UUID a, UUID b) {
		return BSPlayer.byUUID(a).getUnpardonedShares().stream().anyMatch(s ->
			(s.getFirstPlayer().getUUID().equals(a) && s.getSecondPlayer().getUUID().equals(b)) ||
			(s.getFirstPlayer().getUUID().equals(b) && s.getSecondPlayer().getUUID().equals(a)));
	}


}
