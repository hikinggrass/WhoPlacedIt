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
import java.util.ArrayList;
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
			String query = "CREATE TABLE trackedBlocks (id INT AUTO_INCREMENT PRIMARY_KEY, user VARCHAR(255),uuid VARCHAR(255), x INT, y INT, z INT);";
			this.manageSQLite.createTable(query);
		}

	}

	public void save(ArrayList<TrackedBlock> trackedBlocks) {
		try {
			// Create directory and/or file if they don't exist
			boolean directoryCreated = directory.mkdir();
			boolean fileCreated = fileName.createNewFile();
			if (directoryCreated) {
				log.info("[WhoPlacedIt] directory did not exist, created it");
			}
			if (fileCreated) {
				log.info("[WhoPlacedIt] file did not exist, created it");
			}

			// Serialize to a file
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(fileName));
			out.writeObject(trackedBlocks);
			out.close();

		} catch (IOException e) {
		}

	}

	@SuppressWarnings("unchecked")
	public ArrayList<TrackedBlock> load() {
		ArrayList<TrackedBlock> trackedBlocks = null;
		try {
			// Deserialize from a file
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			// Deserialize the object
			trackedBlocks = (ArrayList<TrackedBlock>) in.readObject();
			in.close();

		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}

		return trackedBlocks;

	}

	public void saveSQL(ArrayList<TrackedBlock> trackedBlocks) {

	}

	@SuppressWarnings("unchecked")
	public ArrayList<TrackedBlock> loadSQL() {
		ArrayList<TrackedBlock> trackedBlocks = null;
		try {
			// Deserialize from a file
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			// Deserialize the object
			trackedBlocks = (ArrayList<TrackedBlock>) in.readObject();
			in.close();

		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}

		return trackedBlocks;

	}

	public void placeBlock(Block block, Player player) {
		String query = "INSERT INTO trackedBlocks (user, uuid, x, y, z) VALUES ('" + player.getName() + "','"
				+ player.getUniqueId().toString() + "', " + block.getX() + ", " + block.getY() + ", " + block.getZ()
				+ ");";

		if (this.mode == 1) {

		} else {
			this.manageSQLite.insertQuery(query);
		}
	}

	public void removeBlock(Block block) {
		String query = "DELETE FROM trackedBlocks WHERE x = " + block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + ";";
		if (this.mode == 1) {

		} else {
			this.manageSQLite.deleteQuery(query);
		}
	}

	public String getBlockInfo(Block block) {
		String query = "SELECT * FROM trackedBlocks WHERE x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + ";";
		ResultSet result = null;
		String user = null;

		if (this.mode == 1) {

		} else {
			result = this.manageSQLite.sqlQuery(query);
			try {

				if (result != null && result.next()) {
					user = result.getString("user");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;

	}
}
