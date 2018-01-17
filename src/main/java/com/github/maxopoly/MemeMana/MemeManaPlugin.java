package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.listener.LoginListener;
import com.github.maxopoly.MemeMana.command.MemeManaCommandHandler;
import vg.civcraft.mc.civmodcore.ACivMod;
import org.bukkit.configuration.ConfigurationSection;

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
		instance = this;
		config = new MemeManaConfig(this);
		setupDatabase();
		manaManager = new MemeManaManager();
		activityManager = new PlayerActivityManager(manaManager);
		registerListener();
		// Register commands.
		MemeManaCommandHandler handle = new MemeManaCommandHandler();
		setCommandHandler(handle);
		handle.registerCommands();
	}

	private void setupDatabase() {
		ConfigurationSection config = getConfig().getConfigurationSection("mysql");
		String host = config.getString("host");
		int port = config.getInt("port");
		String user = config.getString("user");
		String pass = config.getString("password");
		String dbname = config.getString("database");
		int poolsize = config.getInt("poolsize");
		long connectionTimeout = config.getLong("connectionTimeout");
		long idleTimeout = config.getLong("idleTimeout");
		long maxLifetime = config.getLong("maxLifetime");
		try {
			dao = new MemeManaDAO(this, user, pass, host, port, dbname, poolsize, connectionTimeout, idleTimeout, maxLifetime);
		} catch(Exception e) {
			warning("Could not connect to database, stopping MemeMana", e);
			getServer().getPluginManager().disablePlugin(this);
		}
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
