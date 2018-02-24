package com.github.maxopoly.MemeMana.model;

public class MemeManaTransferLogEntry {

	public final long timestamp;
	public final int from;
	public final int to;
	public final int manaAmount;

	public MemeManaTransferLogEntry(long timestamp, int from, int to, int manaAmount){
		this.timestamp = timestamp;
		this.from = from;
		this.to = to;
		this.manaAmount = manaAmount;
	}
}
