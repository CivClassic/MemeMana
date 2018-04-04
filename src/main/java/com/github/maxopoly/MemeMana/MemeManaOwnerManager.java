package com.github.maxopoly.MemeMana;

import com.civclassic.altmanager.AltManager;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;

public class MemeManaOwnerManager {

	private Map <OwnerType, Map<Integer, Integer>> ownersByTypeAndExternal;

	//this is barely used during runtime, so this slightly slow loop is fine
	public static OwnerType ownerTypeFromMagicNumber(int magic) {
		return Stream.of(OwnerType.values())
			.filter(t -> t.magicOwnerTypeNumber == magic)
			.findFirst().orElse(null);
	}

	public MemeManaOwnerManager() {
		reloadFromDatabase();
	}

	private void reloadFromDatabase() {
		this.ownersByTypeAndExternal = MemeManaPlugin.getInstance().getDAO().loadAllManaOwners();
	}

	public int getOwnerByExternal(OwnerType ownerType, int externalId) {
		Integer mOwner = ownersByTypeAndExternal.get(ownerType).get(externalId);
		if(mOwner == null){
			MemeManaPlugin.getInstance().getDAO().registerManaOwner(ownerType,externalId);
			mOwner = MemeManaPlugin.getInstance().getDAO().getManaOwnerByForeign(ownerType,externalId);
			ownersByTypeAndExternal.get(ownerType).put(externalId,mOwner);
		}
		return mOwner;
	}

	public static int fromUUID(UUID uuid) {
		return MemeManaPlugin.getInstance().getOwnerManager().getOwnerByExternal(OwnerType.PLAYER_OWNER,AltManager.instance().getAssociationGroup(uuid));
	}

	public static Integer fromPlayerName(String playerName) {
		UUID u = NameAPI.getUUID(playerName);
		return u == null ? null : fromUUID(u);
	}

	public static int fromPlayer(Player player) {
		return fromUUID(player.getUniqueId());
	}

	public static int fromNameLayerGroup(Group grp) {
		return MemeManaPlugin.getInstance().getOwnerManager().getOwnerByExternal(OwnerType.NAMELAYER_GROUP_OWNER,grp.getGroupId());
	}

	public static Integer fromNameLayerGroup(String grpName) {
		return Optional.ofNullable(GroupManager.getGroup(grpName)).map(g -> fromNameLayerGroup(g)).orElse(null);
	}

	public static Integer fromName(String ownerName) {
		Integer ownerId = fromPlayerName(ownerName);
		if(ownerId != null) { return ownerId; }
		return fromNameLayerGroup(ownerName);
	}
}
