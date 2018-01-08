package com.github.maxopoly.MemeMana;

import org.bukkit.configuration.file.FileConfiguration;

public class MemeManaConfig {

	private MemeManaPlugin plugin;

	private long manaRotTime;
	private double decayMultiplier;
	private long manaDecayTime;
	private int maximumDailyMana;

	public MemeManaConfig(MemeManaPlugin plugin) {
		this.plugin = plugin;
		parse();
	}

	public void parse() {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		// 90 day default
		manaRotTime = config.getLong("manaRotTime", 90 * 24 * 60 * 60 * 1000);
		// 30 day default
		manaDecayTime = config.getLong("manaDecayTime", 30 * 24 * 60 * 60 * 1000);
		decayMultiplier = config.getDouble("decayMultiplier", 0.5);
		maximumDailyMana = config.getInt("maxDailyMana", 10);
	}

	/**
	 * @return How many milliseconds it takes mana to fully disappear. -1 for never
	 */
	public long getManaRotTime() {
		return manaRotTime;
	}

	/**
	 * @return The factory by which mana is reduced after the decay interval
	 */
	public double getDecayMultiplier() {
		return decayMultiplier;
	}

	/**
	 * @return How many milliseconds it takes mana to progress through a decay stage
	 */
	public long getManaDecayTime() {
		return manaDecayTime;
	}

	/**
	 * @return How much mana a player can get on a maximum streak
	 */
	public int getMaximumDailyMana() {
		return maximumDailyMana;
	}
}
