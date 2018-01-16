package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.CommandHandler;
public class MemeManaCommandHandler extends CommandHandler{

	@Override
	public void registerCommands() {
		addCommands(new CmdMana("Mana"));
	}
}
