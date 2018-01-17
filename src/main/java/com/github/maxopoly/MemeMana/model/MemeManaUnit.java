package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaConfig;
import com.github.maxopoly.MemeMana.MemeManaPlugin;

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

	public void removeAmount(double amt) {
		double maximumAtThisTime = originalAmount * getDecayMultiplier();
		double percentage = amt / maximumAtThisTime;
		this.fillGrade -= percentage;
	}

	public double getCurrentAmount() {
		return getOriginalAmount() * getFillGrade() * getDecayMultiplier();
	}

	private double getDecayMultiplier() {
		MemeManaConfig config = MemeManaPlugin.getInstance().getManaConfig();
		long currentTime = System.currentTimeMillis();
		return Math.pow(config.getDecayMultiplier(), (currentTime - getGainTime()) / config.getManaDecayTime());
	}
}
