package com.github.maxopoly.MemeMana;
/**
 * Lists owner types, note that the magic numbers are used in the
 * database so they must be stable between versions
 */
public enum OwnerType{
	PLAYER_OWNER(0),
	NAMELAYER_GROUP_OWNER(1);

	public final int magicOwnerTypeNumber;

	private OwnerType(int magic){
		this.magicOwnerTypeNumber = magic;
	}
}
