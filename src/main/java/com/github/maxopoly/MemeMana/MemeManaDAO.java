package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

public class MemeManaDAO extends ManagedDatasource {

	private static final MemeManaOwnerManager ownerManager = MemeManaPlugin.getInstance().getOwnerManager();
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
				"create table if not exists manaOwners (id int auto_increment unique, foreignId int not null, foreignIdType tinyint not null, primary key(foreignId,foreignIdType));",
				"create table if not exists manaUnits (id int not null, baseAmount double not null, fillGrade double not null default 1.0,"
						+ "date timestamp not null default now(), ownerId int not null references manaOwners(id), unique (id), index `ownerIdIndex` (ownerId), primary key(id));",
				"create table if not exists manaStats (ownerId int primary key, streak int not null, lastDay bigint not null);");
	}

	/**
	 * Deletes all mana units older than the mana rot time from the database
	 */
	public void cleanseManaUnits() {
		try (Connection connection = getConnection();
				PreparedStatement cleanseManaUnits = connection
						.prepareStatement("delete from manaUnits where fillGrade < ? or date < ?;")) {
			double epsilon = 0.0001;
			cleanseManaUnits.setDouble(1, epsilon);
			long currentTime = System.currentTimeMillis();
			long rotTime = MemeManaPlugin.getInstance().getManaConfig().getManaRotTime();
			cleanseManaUnits.setTimestamp(2, new Timestamp(currentTime - rotTime));
			cleanseManaUnits.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem cleansing mana units", e);
		}
	}

	/**
	 * Adds a single mana unit, which previously didn't exist, to the database
	 * @param unit Mana unit to add
	 * @param owner Owner of the new mana
	 */
	public void addManaUnit(MemeManaUnit unit, int owner) {
		try (Connection connection = getConnection();
				PreparedStatement addManaUnit = connection
						.prepareStatement("insert into manaUnits (id, baseAmount, fillGrade, date, ownerId) values(?,?,?,?,?);")) {
			addManaUnit.setInt(1, unit.getID());
			addManaUnit.setDouble(2, unit.getOriginalAmount());
			addManaUnit.setDouble(3, unit.getFillGrade());
			addManaUnit.setTimestamp(4, new Timestamp(unit.getGainTime()));
			addManaUnit.setInt(5, owner);
			addManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem adding mana unit", e);
		}
	}

	/**
	 * Deletes a single mana unit from the database
	 * @param unit ManaUnit to delete
	 */
	public void snipeManaUnit(int unit) {
		try (Connection connection = getConnection();
				PreparedStatement snipeManaUnit = connection
						.prepareStatement("delete from manaUnits where id=?;")) {
			snipeManaUnit.setInt(1, unit);
			snipeManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem sniping mana unit", e);
		}
	}

	/**
	 * Updates a mana unit by adjusting it's owner and fillgrade
	 * @param unit
	 * @param newPercentage
	 */
	public void updateManaUnit(MemeManaUnit unit, double newPercentage, int newOwner) {
		try (Connection connection = getConnection();
				PreparedStatement updateManaUnit = connection
						.prepareStatement("update manaUnits set fillGrade=?, set ownerId = ? where id=?;")) {
			updateManaUnit.setDouble(1, newPercentage);
			updateManaUnit.setInt(2, newOwner);
			updateManaUnit.setInt(3, unit.getID());
			updateManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem updating mana unit", e);
		}
	}


	/**
	 * Updates the owner of a given mana unit in the db
	 * @param unit Unit to update owner for
	 * @param newOwner New owner of the mana
	 */
	public void updateManaOwner(MemeManaUnit unit, int newOwner) {
		updateManaUnit(unit, unit.getFillGrade(), newOwner);
	}

	/**
	 * Updates the fill grade of a single mana unit in the db
	 * @param unit
	 */
	public void updateManaFillGrade(MemeManaUnit unit) {
		try (Connection connection = getConnection();
				PreparedStatement updateManaUnit = connection
						.prepareStatement("update manaUnits set fillGrade=? where id=?;")) {
			updateManaUnit.setDouble(1, unit.getFillGrade());
			updateManaUnit.setInt(2, unit.getID());
			updateManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem updating mana unit", e);
		}
	}

	public Map<OwnerType, Map<Integer, Integer>> loadAllManaOwners() {
		Map<OwnerType,Map<Integer,Integer>> owners = new HashMap<OwnerType,Map<Integer,Integer>>();
		for(OwnerType ty : OwnerType.values()){
			owners.put(ty,new HashMap<Integer,Integer>());
		}
		try (Connection connection = getConnection();
				PreparedStatement getManaOwner = connection
						.prepareStatement("select id, foreignId, foreignIdType from manaOwners;");
				ResultSet rs = getManaOwner.executeQuery();) {
			MemeManaPouch thisPouch = new MemeManaPouch();
			int thisOwner = -1;
			while(rs.next()) {
				int id = rs.getInt(1);
				OwnerType ty = MemeManaOwnerManager.ownerTypeFromMagicNumber(rs.getInt(3));
				owners.get(ty).put(rs.getInt(2), id);
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting mana owners", e);
		}
		return owners;
	}

	public void registerManaOwner(OwnerType typ, int foreignId) {
		try (Connection connection = getConnection();
				PreparedStatement addManaOwner = connection
						.prepareStatement("insert into manaOwners (foreignId, foreignIdType) values (?,?);")) {
			addManaOwner.setInt(1, foreignId);
			addManaOwner.setInt(2, typ.magicOwnerTypeNumber);
			addManaOwner.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem registering mana owner", e);
		}
	}

	public Integer getManaOwnerByForeign(OwnerType typ, int foreignId) {
		try (Connection connection = getConnection();
				PreparedStatement getManaOwner = connection
						.prepareStatement("select id from manaOwners where foreignId=? and foreignIdType=?;")) {
			getManaOwner.setInt(1, foreignId);
			getManaOwner.setInt(2, typ.magicOwnerTypeNumber);
			ResultSet rs = getManaOwner.executeQuery();
			rs.first();
			return rs.getInt(1);
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting mana owner by foreign id", e);
			return null;
		}
	}

	public Map<Integer,MemeManaPouch> loadManaPouches() {
		Map<Integer,MemeManaPouch> pouches = new HashMap<Integer,MemeManaPouch>();
		try (Connection connection = getConnection();
				PreparedStatement getManaPouches = connection
						.prepareStatement("select id, baseAmount, fillGrade, date, ownerId from manaUnits order by ownerId, date;");
				ResultSet rs = getManaPouches.executeQuery();) {
			while(rs.next()) {
				pouches.putIfAbsent(rs.getInt(5), new MemeManaPouch());
				pouches.get(rs.getInt(5)).addNewUnit(new MemeManaUnit(rs.getInt(1),rs.getDouble(2),rs.getTimestamp(4).getTime(),rs.getDouble(3)));
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting mana pouches", e);
			return null;
		}
		return pouches;
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

	public void updateManaStat(int owner, ManaGainStat stat) {
		try (Connection connection = getConnection();
				PreparedStatement updateManaStat = connection
						.prepareStatement("replace into manaStats (ownerId, streak, lastDay) values(?,?,?);")) {
			updateManaStat.setInt(1, owner);
			updateManaStat.setInt(2, stat.getStreak());
			updateManaStat.setLong(3, stat.getLastDay());
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
				out.put(rs.getInt(1),new ManaGainStat(rs.getInt(2),rs.getLong(3)));
			}
			return out;
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting mana stats", e);
			return null;
		}
	}
}
