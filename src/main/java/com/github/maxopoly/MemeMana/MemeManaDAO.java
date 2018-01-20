package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

public class MemeManaDAO extends ManagedDatasource {

	private Logger logger;

	public MemeManaDAO(MemeManaPlugin plugin, String user, String pass, String host, int port, String database,
			int poolSize, long connectionTimeout, long idleTimeout, long maxLifetime) {
		super(plugin, user, pass, host, port, database, poolSize, connectionTimeout, idleTimeout, maxLifetime);
		this.logger = plugin.getLogger();
		prepareMigrations();
		updateDatabase();
	}

	/**
	 * Creates and updates tables
	 */
	private void prepareMigrations() {
		registerMigration(
				0,
				false,
				"CREATE TABLE IF NOT EXISTS manaUnits (id int not null, baseAmount double not null, fillGrade double not null default 1.0,"
						+ "date datetime NOT NULL default NOW(), ownerId int not null, ownerIdType enum('PLAYER') not null, unique (id), index `ownerIdIndex` (ownerId));",
				"create table if not exists manaStats (id int not null, idType enum('PLAYER') not null, streak int not null, lastDay bigint not null, primary key(id,idType));");
	}

	public void addManaUnit(MemeManaUnit unit, MemeManaOwner owner) {
		try (Connection connection = getConnection();
				PreparedStatement addManaUnit = connection
						.prepareStatement("insert into manaUnits (id, baseAmount, fillGrade, date, ownerId, ownerIdType) values(?,?,?,?,?,?);")) {
			addManaUnit.setInt(1, unit.getID());
			addManaUnit.setDouble(2, unit.getOriginalAmount());
			addManaUnit.setDouble(3, unit.getFillGrade());
			addManaUnit.setDate(4, new Date(unit.getGainTime()));
			addManaUnit.setInt(5, owner.getID());
			addManaUnit.setString(6, owner.getType().toString());
			addManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem adding mana unit", e);
		}
	}

	// TODO: Don't ignore the owner type here
	public Map<Integer,MemeManaPouch> getManaPouches() {
		try (Connection connection = getConnection();
				PreparedStatement getManaPouches = connection
						.prepareStatement("select * from manaUnits order by ownerId, date;");
				ResultSet rs = getManaPouches.executeQuery();) {
			Map<Integer,MemeManaPouch> out = new HashMap<Integer,MemeManaPouch>();
			MemeManaPouch thisPouch = new MemeManaPouch();
			int thisOwner = -1;
			while(rs.next()) {
				if(thisOwner != rs.getInt(5)) {
					out.put(thisOwner,thisPouch);
					thisPouch = new MemeManaPouch();
					thisOwner = rs.getInt(5);
				}
				thisPouch.addNewUnit(new MemeManaUnit(rs.getInt(1),rs.getDouble(2),rs.getDate(4).getTime(),rs.getDouble(3)));
			}
			out.put(thisOwner,thisPouch);
			return out;
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting mana pouches", e);
			return null;
		}
	}

	public Integer getNextManaId() {
		try (Connection connection = getConnection();
				PreparedStatement getNextMana = connection
						.prepareStatement("select max(id) from manaUnits;");
				ResultSet rs = getNextMana.executeQuery()) {
			rs.first();
			return rs.getInt(1) + 1; // Add one for the *next* mana id
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting next mana id", e);
			return null;
		}
	}
	private void insertChron(MemeManaUnit unit,List<MemeManaUnit> units) {
		int i = units.size() - 1;
		while(i >= 0 && unit.getGainTime() < units.get(i).getGainTime()) {
			i--;
		}
		units.add(i + 1,unit);
	}

	public void updateManaStat(MemeManaOwner owner, ManaGainStat stat) {
		try (Connection connection = getConnection();
				PreparedStatement updateManaStat = connection
						.prepareStatement("replace into manaStats (id, idType, streak, lastDay) values(?,?,?,?);")) {
			updateManaStat.setInt(1, owner.getID());
			updateManaStat.setString(2, owner.getType().toString());
			updateManaStat.setInt(3, stat.getStreak());
			updateManaStat.setLong(4, stat.getLastDay());
			updateManaStat.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem updating mana stat", e);
		}
	}

	// TODO: Don't ignore the owner type here
	public Map<Integer,ManaGainStat> getManaStats() {
		try (Connection connection = getConnection();
				PreparedStatement getManaStats = connection
						.prepareStatement("select * from manaStats;")) {
			ResultSet rs = getManaStats.executeQuery();
			Map<Integer,ManaGainStat> out = new HashMap<Integer,ManaGainStat>();
			while(rs.next()) {
				out.put(rs.getInt(1),new ManaGainStat(rs.getInt(3),rs.getLong(4)));
			}
			return out;
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting mana stats", e);
			return null;
		}
	}
}
