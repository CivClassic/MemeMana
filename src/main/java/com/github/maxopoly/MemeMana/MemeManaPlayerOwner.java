package com.github.maxopoly.MemeMana;

import java.util.Set;
import java.util.UUID;
import vg.civcraft.mc.namelayer.NameAPI;
import com.civclassic.altmanager.AltManager;
import org.bukkit.entity.Player;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;

public class MemeManaPlayerOwner implements MemeManaOwner {
	private int altGroupId;

	public MemeManaPlayerOwner(int altGroupId) {
		this.altGroupId = altGroupId;
	}

	public static MemeManaPlayerOwner fromUUID(UUID uuid) {
		return new MemeManaPlayerOwner(AltManager.instance().getAssociationGroup(uuid));
	}

	public static MemeManaPlayerOwner fromPlayerName(String playerName) {
		return MemeManaPlayerOwner.fromUUID(NameAPI.getUUID(playerName));
	}

	public static MemeManaPlayerOwner fromPlayer(Player player) {
		return MemeManaPlayerOwner.fromUUID(player.getUniqueId());
	}

	public int getID() {
		return altGroupId;
	}

	public OwnerType getType() {
		return MemeManaOwner.OwnerType.PLAYER;
	}
}
