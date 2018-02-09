package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
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
				"create table if not exists manaUnits (manaContent double not null,"
						+ "gainTime timestamp not null default now(), ownerId int not null references manaOwners(id), index `ownerIdIndex` (ownerId), primary key(ownerId,gainTime));",
				"create table if not exists manaStats (ownerId int primary key, streak int not null, lastDay bigint not null);");
	}

	/**
	 * Deletes all mana units older than the mana rot time from the database
	 */
	public void cleanseManaUnits() {
		try (Connection connection = getConnection();
				PreparedStatement cleanseManaUnits = connection
						.prepareStatement("delete from manaUnits where gainTime < ?;")) {
			long currentTime = System.currentTimeMillis();
			long rotTime = MemeManaPlugin.getInstance().getManaConfig().getManaRotTime();
			cleanseManaUnits.setTimestamp(1, new Timestamp(currentTime - rotTime));
			cleanseManaUnits.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem cleansing mana units", e);
		}
	}

	/**
	 * Adds a single mana unit, to the database. On duplicate, merges by adding mana contents
	 */
	public void addManaUnit(double manaContent, int owner, long timestamp) {
		try (Connection connection = getConnection();
				PreparedStatement addManaUnit = connection
						.prepareStatement("insert into manaUnits (manaContent, gainTime, ownerId) values (?,?,?) on duplicate key update gainTime = gainTime, ownerId = ownerId, manaContent = values(manaContent) + manaContent;")) {
			addManaUnit.setDouble(1, manaContent);
			addManaUnit.setTimestamp(2, new Timestamp(timestamp));
			addManaUnit.setInt(3, owner);
			addManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem adding mana unit", e);
		}
	}

	/**
	 * Deletes a single mana unit from the database by owner and timestamp
	 * @param unit ManaUnit to delete
	 */
	public void snipeManaUnit(int ownerId, long timestamp) {
		try (Connection connection = getConnection();
				PreparedStatement snipeManaUnit = connection
						.prepareStatement("delete from manaUnits where ownerId=? and gainTime=?;")) {
			snipeManaUnit.setInt(1, ownerId);
			snipeManaUnit.setTimestamp(2, new Timestamp(timestamp));
			snipeManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem sniping mana unit", e);
		}
	}

	// Adjust the mana content of a single unit
	public void adjustManaUnit(int ownerId, long timestamp, double newManaContent) {
		try (Connection connection = getConnection();
				PreparedStatement updateManaUnit = connection
						.prepareStatement("update manaUnits set manaContent=? where ownerId=? and gainTime=?;")) {
			updateManaUnit.setDouble(1, newManaContent);
			updateManaUnit.setInt(2, ownerId);
			updateManaUnit.setTimestamp(3, new Timestamp(timestamp));
			updateManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem adjusting mana unit mana content", e);
		}
	}

	public void transferUnitsUntil(int oldOwnerId, int newOwnerId, long timestamp) {
		try (Connection connection = getConnection();
				PreparedStatement transferUnits = connection
						.prepareStatement("update manaUnits set ownerId=? where ownerId=? and gainTime<=?;")) {
			transferUnits.setInt(1, newOwnerId);
			transferUnits.setInt(2, oldOwnerId);
			transferUnits.setTimestamp(3, new Timestamp(timestamp));
			transferUnits.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem transferring mana units until specific timestamp", e);
		}
	}


	public void deleteUnitsUntil(int ownerId, long timestamp) {
		try (Connection connection = getConnection();
				PreparedStatement removePrevious = connection
						.prepareStatement("delete from manaUnits where ownerId=? and gainTime<=?;")) {
			removePrevious.setInt(1, ownerId);
			removePrevious.setTimestamp(2, new Timestamp(timestamp));
			removePrevious.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem deleting mana units until specific timestamp", e);
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

	public void loadManaPouch(int ownerId, Map<Long,Double> units) {
		try (Connection connection = getConnection();
				PreparedStatement getManaPouches = connection
						.prepareStatement("select manaContent, gainTime from manaUnits where ownerId=? order by gainTime;");){
			getManaPouches.setInt(1, ownerId);
			ResultSet rs = getManaPouches.executeQuery();
			while(rs.next()) {
				units.put(rs.getTimestamp("gainTime").getTime(), rs.getDouble("manaContent"));
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem loading a mana pouch", e);
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
