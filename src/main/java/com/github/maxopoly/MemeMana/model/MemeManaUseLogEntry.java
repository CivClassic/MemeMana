package com.github.maxopoly.MemeMana.model;

import java.util.UUID;

public class MemeManaUseLogEntry {

	public final long timestamp;
	public final UUID creator;
	public final UUID fueler;
	public final UUID pearled;
	public final boolean isUpgrade;
	public final int manaAmount;

	public MemeManaUseLogEntry(long timestamp, UUID creator, UUID fueler, UUID pearled, boolean isUpgrade, int manaAmount) {
		this.timestamp = timestamp;
		this.creator = creator;
		this.fueler = fueler;
		this.pearled = pearled;
		this.isUpgrade = isUpgrade;
		this.manaAmount = manaAmount;
	}
}
