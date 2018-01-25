package com.github.maxopoly.MemeMana.model;

public interface MemeManaOwner {
	public static enum OwnerType {
		PLAYER;
	}
	public int getID();
	public OwnerType getType();
}
