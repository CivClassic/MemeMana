package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.listener.LoginListener;
import vg.civcraft.mc.civmodcore.ACivMod;

public class MemeManaPlugin extends ACivMod {

	private static MemeManaPlugin instance;

	private MemeManaConfig config;
	private PlayerActivityManager activityManager;
	private MemeManaManager manaManager;

	public static MemeManaPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		config = new MemeManaConfig(this);
		manaManager = new MemeManaManager();
		activityManager = new PlayerActivityManager();
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

}
