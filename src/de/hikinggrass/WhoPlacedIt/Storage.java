package de.hikinggrass.WhoPlacedIt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This Class manages the storage of the data that is produced in this plugin, at the moment the data is saved in a
 * simple file, later this data will be put into a database (mysql/sqlite)
 */
public class Storage {

	static String mainDirectory = "plugins" + File.separator + "WhoPlacedIt";
	static File directory = new File(mainDirectory);
	static File fileName = new File(mainDirectory + File.separator + "TrackedBlocks.dat");

	Logger log;

	/**
	 * 
	 */
	public Storage(Logger log) {
		this.log = log;
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

}
