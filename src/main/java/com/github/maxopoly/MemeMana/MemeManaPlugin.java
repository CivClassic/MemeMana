package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.command.MemeManaCommandHandler;
import com.github.maxopoly.MemeMana.listener.LoginListener;
import vg.civcraft.mc.civmodcore.ACivMod;

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
