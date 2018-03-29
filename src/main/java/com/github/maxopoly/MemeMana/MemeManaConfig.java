package com.github.maxopoly.MemeMana;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import com.devotedmc.ExilePearl.PearlType;
import vg.civcraft.mc.civmodcore.util.ConfigParsing;

public class MemeManaConfig {

	private MemeManaPlugin plugin;

	private long manaRotTime;
	private long manaWaitTime;
	private int maximumDailyMana;

	public MemeManaConfig(MemeManaPlugin plugin) {
		this.plugin = plugin;
		parse();
	}

	public void parse() {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		// 50 millis per tick
		manaRotTime = ConfigParsing.parseTime(config.getString("manaRotTime", "90d")) * 50L;
		manaWaitTime = ConfigParsing.parseTime(config.getString("manaWaitTime", "30m"));
		maximumDailyMana = config.getInt("maxDailyMana", 10);
	}

	public MemeManaDAO setupDatabase() {
		ConfigurationSection config = plugin.getConfig().getConfigurationSection("mysql");
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
			return new MemeManaDAO(plugin, user, pass, host, port, dbname, poolsize, connectionTimeout, idleTimeout, maxLifetime);
		} catch(Exception e) {
			plugin.warning("Could not connect to database, stopping MemeMana", e);
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return null;
		}
	}

	/**
	 * @return How many milliseconds it takes mana to fully disappear. -1 for never
	 */
	public long getManaRotTime() {
		return manaRotTime;
	}

	/**
	 * @return How much mana a player can get on a maximum streak
	 */
	public int getMaximumDailyMana() {
		return maximumDailyMana;
	}

	/**
	 * @return How long a player must wait after logging in to get their mana
	 */
	public long getManaWaitTime() {
		return manaWaitTime;
	}

	/**
	 * @return How much a unit of mana refills a pearl of the given type
	 */
	public int getPearlRefillAmount(PearlType type) {
		return plugin.getConfig().getInt("pearlRefillAmount." + type, 5);
	}

	/**
	 * @return How much it costs to upgrade Exile => Prison pearl
	 */
	public int getPearlUpgradeAmount() {
		return plugin.getConfig().getInt("pearlUpgradeAmount", 10);
	}
}
