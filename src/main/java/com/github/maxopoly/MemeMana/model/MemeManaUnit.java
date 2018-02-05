package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaConfig;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class MemeManaUnit {

	private int id;
	private double originalAmount;
	private long gainTime;
	private double fillGrade;

	public MemeManaUnit(int id, double originalAmount, long gainTime, double fillGrade) {
		this.id = id;
		this.originalAmount = originalAmount;
		this.gainTime = gainTime;
		this.fillGrade = fillGrade;
	}

	public MemeManaUnit(int id, double originalAmount) {
		this(id, originalAmount, System.currentTimeMillis(), 1.0f);
	}

	/**
	 * @return Timestamp of when the MemeMana was given out
	 */
	public long getGainTime() {
		return gainTime;
	}

	/**
	 * @return How full this unit is on a scale from 1.0 (full) to 0.0 (empty)
	 */
	public double getFillGrade() {
		return fillGrade;
	}

	/**
	 * @return Unique mana unit id used in the data base
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return Original amount that was given
	 */
	public double getOriginalAmount() {
		return originalAmount;
	}

	public void setFillGrade(double fillGrade) {
		this.fillGrade = fillGrade;
	}

	public double getCurrentAmount() {
		return getOriginalAmount() * getFillGrade() * getDecayMultiplier();
	}

	double getDecayMultiplier() {
		MemeManaConfig config = MemeManaPlugin.getInstance().getManaConfig();
		long currentTime = System.currentTimeMillis();
		return Math.pow(config.getDecayMultiplier(), (currentTime - getGainTime()) / config.getManaDecayTime());
	}

	// True means include the date the mana was gained.
	public ItemStack getItemStackRepr(boolean withDate){
		ItemStack i = new ItemStack(Material.EYE_OF_ENDER,(int) getCurrentAmount());
		ItemMeta meta = i.getItemMeta();
		List<String> lore = meta.getLore();
		if(lore == null){
			lore = new ArrayList<String>();
		}
		lore.add("Mana");
		if(withDate){
			lore.add(new Date(gainTime).toString());
		}
		meta.setLore(lore);
		i.setItemMeta(meta);
		return i;
	}
}
