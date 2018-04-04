package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaTransferLogEntry;
import com.github.maxopoly.MemeMana.model.MemeManaUseLogEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
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
				"create table if not exists manaOwners (id int auto_increment unique, foreignId int not null, foreignIdType tinyint not null, primary key(foreignId,foreignIdType));",
				"create table if not exists manaUnits (manaContent int not null,"
						+ "gainTime bigint not null, ownerId int not null references manaOwners(id), creator int not null references manaUUIDs(manaLogId), index `ownerIdIndex` (ownerId), primary key(ownerId,gainTime));",
				"create table if not exists manaStats (ownerId int primary key, streak int not null, lastDay bigint not null);",
				"create table if not exists manaLog (logTime bigint not null, fromId int not null references manaOwners(id), toId int not null references manaOwners(id), manaAmount int not null, primary key(logTime,fromId,toId));",
				"create table if not exists manaUseLog (logTime bigint not null, creator int not null references manaUUIDs(manaLogId), user int not null references manaUUIDs(manaLogId), pearled int not null references manaUUIDs(manaLogId), upgrade boolean not null, mana int not null, primary key(logTime,creator,user,pearled,upgrade));",
				"create table if not exists manaUUIDs (manaLogId int not null auto_increment, manaLogUUID varchar(40) unique not null, primary key(manaLogId));");
	}

	public void logManaTransfer(int fromId, int toId, int manaAmount){
		try (Connection connection = getConnection();
				PreparedStatement ps = connection
						.prepareStatement("insert into manaLog (fromId, toId, manaAmount, logTime) values (?,?,?,?) on duplicate key update logTime = logTime, fromId = fromId, toId = toId, manaAmount = values(manaAmount) + manaAmount;")) {
			ps.setInt(1, fromId);
			ps.setInt(2, toId);
			ps.setInt(3, manaAmount);
			ps.setLong(4, new Date().getTime());
			ps.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem logging mana transfer", e);
		}
	}

	public void logManaUse(UUID creator, UUID user, UUID pearled, int amount, boolean isUpgrade, long timestamp){
		registerUUID(creator);
		registerUUID(user);
		registerUUID(pearled);
		try (Connection connection = getConnection();
				PreparedStatement ps = connection
						.prepareStatement("insert into manaUseLog (creator, user, pearled, upgrade, mana, logTime) values ((select manaLogId from manaUUIDs where manaLogUUID = ?), (select manaLogId from manaUUIDs where manaLogUUID = ?), (select manaLogId from manaUUIDs where manaLogUUID = ?), ?, ?, ?) on duplicate key update creator = manaUseLog.creator, user = manaUseLog.user, pearled = manaUseLog.pearled, upgrade = manaUseLog.upgrade, mana = ? + manaUseLog.mana;")) {
			ps.setString(1, creator.toString());
			ps.setString(2, user.toString());
			ps.setString(3, pearled.toString());
			ps.setBoolean(4, isUpgrade);
			ps.setInt(5, amount);
			ps.setLong(6, timestamp);
			ps.setInt(7, amount);
			ps.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem logging mana use", e);
		}
	}

	public Stream<MemeManaTransferLogEntry> getTransferLog(int owner) {
		try (Connection connection = getConnection();
				PreparedStatement ps = connection
						.prepareStatement("select * from manaLog where fromId = ? or toId = ?;")) {
			ps.setInt(1,owner);
			ps.setInt(2,owner);
			ResultSet rs = ps.executeQuery();
			Stream.Builder<MemeManaTransferLogEntry> b = Stream.builder();
			while(rs.next()){
				b.accept(new MemeManaTransferLogEntry(rs.getLong(1),rs.getInt(2),rs.getInt(3),rs.getInt(4)));
			}
			return b.build();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting creator UUID", e);
		}
		return null;
	}

	public Stream<MemeManaUseLogEntry> getUseLog(UUID pearled) {
		try (Connection connection = getConnection();
				PreparedStatement getCreatorUUID = connection
						.prepareStatement("select l.logTime, c.manaLogUUID, u.manaLogUUID, p.manaLogUUID, l.upgrade, l.mana from manaUseLog l join manaUUIDs p on p.manaLogId = l.pearled join manaUUIDs c on c.manaLogId = l.creator join manaUUIDs u on u.manaLogId = l.user where p.manaLogUUID = ?;")) {
			getCreatorUUID.setString(1,pearled.toString());
			ResultSet rs = getCreatorUUID.executeQuery();
			Stream.Builder<MemeManaUseLogEntry> b = Stream.builder();
			while(rs.next()){
				b.accept(new MemeManaUseLogEntry(rs.getLong(1),UUID.fromString(rs.getString(2)),UUID.fromString(rs.getString(3)),UUID.fromString(rs.getString(4)),rs.getBoolean(5),rs.getInt(6)));
			}
			return b.build();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting creator UUID", e);
		}
		return null;
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
			cleanseManaUnits.setLong(1, currentTime - rotTime);
			cleanseManaUnits.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem cleansing mana units", e);
		}
	}

	public void registerUUID(UUID u) {
		try (Connection connection = getConnection();
				PreparedStatement getCreatorUUID = connection
						.prepareStatement("insert ignore into manaUUIDs (manaLogUUID) values (?);")) {
			getCreatorUUID.setString(1,u.toString());
			getCreatorUUID.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem registering UUID", e);
		}
	}

	public UUID getCreatorUUID(int owner, long timestamp) {
		try (Connection connection = getConnection();
				PreparedStatement getCreatorUUID = connection
						.prepareStatement("select manaLogUUID from manaUUIDs join manaUnits on manaUUIDs.manaLogId = manaUnits.creator where manaUnits.gainTime = ? and manaUnits.ownerId = ?;")) {
			getCreatorUUID.setLong(1,timestamp);
			getCreatorUUID.setInt(2,owner);
			ResultSet rs = getCreatorUUID.executeQuery();
			if(rs.first()){
				return UUID.fromString(rs.getString(1));
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting creator UUID", e);
		}
		return null;
	}

	/**
	 * Adds a single mana unit, to the database. On duplicate, merges by adding mana contents
	 */
	public void addManaUnit(int manaContent, int owner, long timestamp, UUID creator) {
		registerUUID(creator);
		try (Connection connection = getConnection();
				PreparedStatement addManaUnit = connection
						.prepareStatement("insert into manaUnits (manaContent, gainTime, ownerId, creator) select ?, ?, ?, manaUUIDs.manaLogId from manaUUIDs where manaUUIDs.manaLogUUID = ? on duplicate key update gainTime = manaUnits.gainTime, ownerId = manaUnits.ownerId, manaContent = ? + manaUnits.manaContent, creator = manaUnits.creator;")) {
			addManaUnit.setInt(1, manaContent);
			addManaUnit.setLong(2, timestamp);
			addManaUnit.setInt(3, owner);
			addManaUnit.setString(4, creator.toString());
			addManaUnit.setInt(5, manaContent);
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
			snipeManaUnit.setLong(2, timestamp);
			snipeManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem sniping mana unit", e);
		}
	}

	// Adjust the mana content of a single unit
	public void adjustManaUnit(int ownerId, long timestamp, int newManaContent) {
		try (Connection connection = getConnection();
				PreparedStatement updateManaUnit = connection
						.prepareStatement("update manaUnits set manaContent=? where ownerId=? and gainTime=?;")) {
			updateManaUnit.setInt(1, newManaContent);
			updateManaUnit.setInt(2, ownerId);
			updateManaUnit.setLong(3, timestamp);
			updateManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem adjusting mana unit mana content", e);
		}
	}

	public void transferUnitsUntil(int oldOwnerId, int newOwnerId, long timestamp) {
		try (Connection connection = getConnection();
				PreparedStatement transferUnits = connection
						.prepareStatement("insert into manaUnits (gainTime, ownerId, creator, manaContent) select l.gainTime, ?, l.creator, l.manaContent from manaUnits l where ownerId=? and gainTime<=? on duplicate key update manaContent = l.manaContent + manaUnits.manaContent;")) {
			transferUnits.setInt(1, newOwnerId);
			transferUnits.setInt(2, oldOwnerId);
			transferUnits.setLong(3, timestamp);
			transferUnits.execute();
			deleteUnitsUntil(oldOwnerId, timestamp);
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem transferring mana units until specific timestamp", e);
		}
	}


	public void deleteUnitsUntil(int ownerId, long timestamp) {
		try (Connection connection = getConnection();
				PreparedStatement removePrevious = connection
						.prepareStatement("delete from manaUnits where ownerId=? and gainTime<=?;")) {
			removePrevious.setInt(1, ownerId);
			removePrevious.setLong(2, timestamp);
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

	public void loadManaPouch(int ownerId, Map<Long,Integer> units) {
		try (Connection connection = getConnection();
				PreparedStatement getManaPouches = connection
						.prepareStatement("select manaContent, gainTime from manaUnits where ownerId=? order by gainTime;");){
			getManaPouches.setInt(1, ownerId);
			ResultSet rs = getManaPouches.executeQuery();
			while(rs.next()) {
				units.put(rs.getLong("gainTime"), rs.getInt("manaContent"));
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem loading a mana pouch", e);
		}
	}

	public void updateManaStat(int owner, ManaGainStat stat) {
		try (Connection connection = getConnection();
				PreparedStatement updateManaStat = connection
						.prepareStatement("replace into manaStats (ownerId, streak, lastDay) values(?,?,?)")) {
			updateManaStat.setInt(1, owner);
			updateManaStat.setInt(2, stat.getStreakField());
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
