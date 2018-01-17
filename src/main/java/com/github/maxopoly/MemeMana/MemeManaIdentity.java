package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import java.util.Set;
import java.util.UUID;
import vg.civcraft.mc.namelayer.NameAPI;
import com.programmerdan.minecraft.banstick.data.BSPlayer;
import org.bukkit.entity.Player;

public class MemeManaIdentity implements MemeManaOwner {
	private long bsId;

	public MemeManaIdentity(long bsId) {
		this.bsId = bsId;
	}

	public static MemeManaIdentity fromUUID(UUID uuid) {
		return new MemeManaIdentity(BSPlayer.byUUID(uuid).getId());
	}

	public static MemeManaIdentity fromPlayerName(String playerName) {
		return MemeManaIdentity.fromUUID(NameAPI.getUUID(playerName));
	}

	public static MemeManaIdentity fromPlayer(Player player) {
		return MemeManaIdentity.fromUUID(player.getUniqueId());
	}

	public long getID() {
		return bsId;
	}

	public long selectAlt(Set<Long> theSet) {
		Long found = null;
		for(long u : theSet) {
			if(this.associatedWith(new MemeManaIdentity(u))) {
				if(found == null) {
					found = u;
				} else {
					theSet.remove(u);
				}
			}
		}
		return found;
	}

	public boolean associatedWith(MemeManaOwner other) {
		return BSPlayer.byId(bsId).getUnpardonedShares().stream().anyMatch(s ->
			(s.getFirstPlayer().getId() == bsId && s.getSecondPlayer().getId() == other.getID()) ||
			(s.getFirstPlayer().getId() == other.getID() && s.getSecondPlayer().getId() == bsId));
	}


}
