package de.hikinggrass.WhoPlacedIt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.alta189.sqlLibrary.SQLite.sqlCore;

/**
 * This Class manages the storage of the data that is produced in this plugin, at the moment the data is saved in a
 * simple file, later this data will be put into a database (mysql/sqlite)
 */
public class Storage {

	static String mainDirectory = "plugins" + File.separator + "WhoPlacedIt";
	static File directory = new File(mainDirectory);
	static File fileName = new File(mainDirectory + File.separator + "TrackedBlocks.dat");

	private int mode; // 0 = File, 1 = MySQL, 2 = SQLite

	Logger log;

	public sqlCore manageSQLite;

	public Boolean MySQL = false;

	/**
	 * 
	 */
	public Storage(Logger log, int mode) {
		this.log = log;
		this.mode = mode;

		this.log.info("SQLite Initializing");

		// Declare SQLite handler
		this.manageSQLite = new sqlCore(this.log, "[SQL INFO]", "WhoPlacedIt", directory.getPath());

		// Initialize SQLite handler
		this.manageSQLite.initialize();

		// Check if the table exists, if it doesn't create it
		if (!this.manageSQLite.checkTable("trackedBlocks")) {
			this.log.info("Creating table trackedBlocks");
			String query = "CREATE TABLE trackedBlocks (id INT AUTO_INCREMENT PRIMARY_KEY, user VARCHAR(255), uuid VARCHAR(255), x INT, y INT, z INT, createTime BIGINT, removeTime BIGINT);";
			this.manageSQLite.createTable(query);
		}

	}

	public void placeBlock(Block block, Player player, long createTime) {
		String query = "INSERT INTO trackedBlocks (user, uuid, x, y, z, createTime, removeTime) VALUES ('"
				+ player.getName() + "','" + player.getUniqueId().toString() + "', " + block.getX() + ", "
				+ block.getY() + ", " + block.getZ() + ", " + createTime + ", " + 0 + ");";

		if (this.mode == 1) {

		} else {
			this.manageSQLite.insertQuery(query);
		}
	}

	public void removeBlock(Block block, long removeTime) {
		String query = "UPDATE trackedBlocks SET removeTime = " + removeTime + " WHERE x = " + block.getX()
				+ " AND y = " + block.getY() + " AND z = " + block.getZ() + " AND removeTime = 0;";
		if (this.mode == 1) {

		} else {
			this.manageSQLite.updateQuery(query);
		}
	}

	public ArrayList<String> getBlockInfo(Block block) {
		String query = "SELECT * FROM trackedBlocks WHERE x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " LIMIT 3;";
		ResultSet result = null;
		ArrayList<String> user = new ArrayList<String>();

		if (this.mode == 1) {

		} else {
			try {
				result = this.manageSQLite.sqlQuery(query);

				while (result != null && result.next()) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					Date resultCreateDate = new Date(result.getLong("createTime"));
					Date resultRemoveDate = new Date(result.getLong("removeTime"));

					user.add(result.getString("user") + " created on " + sdf.format(resultCreateDate) + " deleted on "
							+ sdf.format(resultRemoveDate));

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;

	}
}
