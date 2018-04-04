package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.command.CmdManaWithdraw;
import com.github.maxopoly.MemeMana.command.MemeManaCommandHandler;
import com.github.maxopoly.MemeMana.listener.LoginListener;
import com.github.maxopoly.MemeMana.listener.PearlListener;
import java.util.LinkedList;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.namelayer.GroupManager.PlayerType;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class MemeManaPlugin extends ACivMod {

	private static MemeManaPlugin instance;

	private MemeManaConfig config;
	private PlayerActivityManager activityManager;
	private MemeManaDAO dao;
	private MemeManaOwnerManager ownerManager;

	public static MemeManaPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		config = new MemeManaConfig(this);
		dao = config.setupDatabase();
		dao.cleanseManaUnits();
		registerNameLayerPerm();
		activityManager = new PlayerActivityManager();
		ownerManager = new MemeManaOwnerManager();
		registerListener();
		// Register commands.
		MemeManaCommandHandler handle = new MemeManaCommandHandler();
		setCommandHandler(handle);
		handle.registerCommands();
	}

	private void registerListener() {
		getServer().getPluginManager().registerEvents(new LoginListener(), this);
		getServer().getPluginManager().registerEvents(new PearlListener(), this);
	}

	// By default, admins and owners have access to take mana.
	private void registerNameLayerPerm(){
		LinkedList<PlayerType> defaultPerms = new LinkedList<PlayerType>();
		defaultPerms.add(PlayerType.ADMINS);
		defaultPerms.add(PlayerType.OWNER);
		PermissionType.registerPermission(CmdManaWithdraw.withdrawPermissionName, defaultPerms);
	}

	@Override
	protected String getPluginName() {
		return "MemeMana";
	}

	public MemeManaConfig getManaConfig() {
		return config;
	}

	public PlayerActivityManager getActivityManager() {
		return activityManager;
	}

	public MemeManaDAO getDAO() {
		return dao;
	}

	public MemeManaOwnerManager getOwnerManager() {
		return ownerManager;
	}
}
