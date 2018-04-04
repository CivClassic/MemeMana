package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.command.CmdExilePearl;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import org.bukkit.Bukkit;
import vg.civcraft.mc.civmodcore.command.CommandHandler;

public class MemeManaCommandHandler extends CommandHandler{

	@Override
	public void registerCommands() {
		addCommands(new CmdManaInspect("CmdManaInspect"));
		addCommands(new CmdManaIncrease("CmdManaIncrease"));
		addCommands(new CmdManaReset("CmdManaReset"));
		addCommands(new CmdManaShow("CmdManaShow"));
		addCommands(new CmdManaTransfer("CmdManaTransfer"));
		addCommands(new CmdManaMaterialize("CmdManaMaterialize"));
		addCommands(new CmdManaWithdraw("CmdManaWithdraw"));
		addCommands(new CmdManaSimulateLogin("CmdManaSimulateLogin"));
		addCommands(new CmdManaFuelLog("CmdManaFuelLog"));
		addCommands(new CmdManaTransactionLog("CmdManaTransactionLog"));
		addCommands(new CmdManaClaim("CmdManaClaim"));

		if(Bukkit.getPluginManager().isPluginEnabled("ExilePearl")){
			CmdExilePearl.instance().addSubCommand(new CmdManaRefill(MemeManaPlugin.getInstance()));
			CmdExilePearl.instance().addSubCommand(new CmdManaUpgrade(MemeManaPlugin.getInstance()));
		}
	}
}
