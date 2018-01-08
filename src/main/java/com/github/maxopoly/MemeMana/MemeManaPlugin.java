package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.listener.LoginListener;
import vg.civcraft.mc.civmodcore.ACivMod;

public class MemeManaPlugin extends ACivMod {

	private static MemeManaPlugin instance;

	private MemeManaConfig config;
	private PlayerActivityManager activityManager;
	private MemeManaManager manaManager;
	private MemeManaDAO dao;

	public static MemeManaPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		config = new MemeManaConfig(this);
		// TODO create dao instance
		manaManager = new MemeManaManager();
		activityManager = new PlayerActivityManager(manaManager);
		registerListener();
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

	public MemeManaManager getManaManager() {
		return manaManager;
	}

	public MemeManaDAO getDAO() {
		return dao;
	}

}
