package de.hikinggrass.WhoPlacedIt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import java.lang.Boolean;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.alta189.sqlLibrary.MySQL.mysqlCore;
import com.alta189.sqlLibrary.SQLite.sqlCore;

/**
 * This Class manages the storage of the data that is stored into a database
 * (mysql/sqlite)
 */
public class Storage {

    protected static String mainDirectory = "plugins" + File.separator + "WhoPlacedIt";
    protected static File directory = new File(mainDirectory);
    protected static File fileName = new File(mainDirectory + File.separator + "WhoPlacedIt.properties");
    protected int mode; // 1 = MySQL, 2 = SQLite
    protected ArrayList<Integer> inHand;
    protected Properties properties = new Properties();
    protected Logger log;
    protected sqlCore manageSQLite;
    protected mysqlCore manageMySQL;
    protected String mysqlHost;
    protected String mysqlUser;
    protected String mysqlPassword;
    protected String mysqlDatabase;

    /**
     *
     */
    public Storage(Logger log) {
        String query = "";
        String queryExt = "";
        ResultSet result = null;
        this.log = log;
        this.inHand = new ArrayList<Integer>();
        this.loadProperties();

        if (this.properties.getProperty("database") != null && this.properties.getProperty("database").equals("mysql")) {
            if (this.properties.getProperty("mysqlHost") == null || this.properties.getProperty("mysqlHost").equals("")
                    || this.properties.getProperty("mysqlUser") == null
                    || this.properties.getProperty("mysqlUser").equals("")
                    || this.properties.getProperty("mysqlPassword") == null
                    || this.properties.getProperty("mysqlPassword").equals("")
                    || this.properties.getProperty("mysqlDatabase") == null
                    || this.properties.getProperty("mysqlDatabase").equals("")) {
                this.mode = 2;
            } else {
                this.mode = 1;
            }

        } else {
            this.mode = 2;
        }

        if (this.mode == 1) {
            this.log.info("[WhoPlacedIt] MySQL Initializing");
            this.mysqlHost = properties.getProperty("mysqlHost");
            this.mysqlUser = properties.getProperty("mysqlUser");
            this.mysqlPassword = properties.getProperty("mysqlPassword");
            this.mysqlDatabase = properties.getProperty("mysqlDatabase");

            log.info("[WhoPlacedIt] using database " + this.mysqlDatabase);

            // Declare MySQL Handler
            this.manageMySQL = new mysqlCore(this.log, "[WhoPlacedIt]", this.mysqlHost, this.mysqlDatabase,
                    this.mysqlUser, this.mysqlPassword);

            // Initialize MySQL Handler
            this.manageMySQL.initialize();

            try {
                if (this.manageMySQL.checkConnection()) { // Check if the Connection was successful
                    this.log.info("[WhoPlacedIt] MySQL connection successful");

                    if (!this.manageMySQL.checkTable("trackedBlocks")) {
                        // Check if the table exists in the database if
                        // not create it
                        this.log.info("[WhoPlacedIt] Creating table trackedBlocks");

                        query = "CREATE TABLE IF NOT EXISTS `trackedBlocks` ( `id` int(11) NOT NULL AUTO_INCREMENT, "
                                + "`createPlayer` varchar(255) DEFAULT NULL, `createPlayerUUID` varchar(32) DEFAULT NULL, "
                                + "`removePlayer` varchar(255) DEFAULT NULL, `removePlayerUUID` varchar(32) DEFAULT NULL, "
                                + "`x` DOUBLE DEFAULT NULL, `y` DOUBLE DEFAULT NULL, `z` DOUBLE DEFAULT NULL, "
                                + "`createTime` bigint(20) DEFAULT NULL, `removeTime` bigint(20) DEFAULT NULL, "
                                + "`cause` varchar(255) NOT NULL DEFAULT 'player', "
                                + "`blockTypeID` int(11) NOT NULL DEFAULT -1,"
                                + "PRIMARY KEY (`id`), "
                                + "KEY `placedBlockCount` (`createTime`,`createPlayerUUID`), "
                                + "KEY `removedBlockCount` (`removeTime`,`removePlayerUUID`), "
                                + "KEY `xyz` (`x`,`y`,`z`), KEY `xyzremove` (`x`,`y`,`z`,`removeTime`)) ENGINE=MyISAM;";

                        this.manageMySQL.createTable(query); // Use mysqlCore.createTable(query) to create tables
                    }

                    this.log.info("[WhoPlacedIt] Checking if Database is up-to-date..");
                    //Check if database has the right columns..
                    query = "SHOW COLUMNS FROM `trackedBlocks`;";
                    result = null;
                    result = this.manageMySQL.sqlQuery(query);

                    Boolean hasCause = false;
                    Boolean hasBlockTypeID = false;
                    Boolean isXdouble = false;
                    Boolean isYdouble = false;
                    Boolean isZdouble = false;

                    //loop through columns..
                    while (result != null && result.next()) {
                        String field = "";
                        String type = "";
                        field = result.getString("Field");
                        type = result.getString("Type");
                        if ("cause".equals(field)) {
                            hasCause = true;
                        } else if ("blockTypeID".equals(field)) {
                            hasBlockTypeID = true;
                        }
                        if ("x".equals(field)) {
                            if ("double".equals(type)) {
                                isXdouble = true;
                            }
                        }
                        if ("y".equals(field)) {
                            if ("double".equals(type)) {
                                isYdouble = true;
                            }
                        }
                        if ("z".equals(field)) {
                            if ("double".equals(type)) {
                                isZdouble = true;
                            }
                        }
                    }

                    if (!isXdouble || !isYdouble || !isZdouble) {
                        query = "ALTER TABLE  `trackedBlocks` ";
                        //CHANGE  `x`  `x` DOUBLE NULL DEFAULT NULL
                        if (!isXdouble) {
                            queryExt += "CHANGE  `x`  `x` DOUBLE NULL DEFAULT NULL ";
                        }
                        //CHANGE  `y`  `y` DOUBLE NULL DEFAULT NULL
                        if (!isYdouble) {
                            if (!"".equals(queryExt)) {
                                queryExt += ", ";
                            }
                            queryExt += "CHANGE  `y`  `y` DOUBLE NULL DEFAULT NULL";
                        }
                        //CHANGE  `z`  `z` DOUBLE NULL DEFAULT NULL
                        if (!isZdouble) {
                            if (!"".equals(queryExt)) {
                                queryExt += ", ";
                            }
                            queryExt += "CHANGE  `z`  `z` DOUBLE NULL DEFAULT NULL";
                        }
                        this.log.info("[WhoPlacedIt] Updating Database fields..");
                        this.manageMySQL.updateQuery(query + queryExt);
                    }
                    //add missing columns..
                    if (!hasCause) {
                        this.log.info("[WhoPlacedIt] Missing Database field 'cause', adding it");
                        query = "ALTER TABLE `trackedBlocks` ADD `cause` VARCHAR(255) NOT NULL DEFAULT 'player';";
                        this.manageMySQL.updateQuery(query);
                    }

                    if (!hasBlockTypeID) {
                        this.log.info("[WhoPlacedIt] Missing Database field 'blockTypeID', adding it");
                        query = "ALTER TABLE `trackedBlocks` ADD `blockTypeID` INT NOT NULL DEFAULT -1;";
                        this.manageMySQL.updateQuery(query);
                    }

                    //check if indexes are there..
                    query = "SHOW INDEX FROM `trackedBlocks`";
                    result = null;
                    result = this.manageMySQL.sqlQuery(query);

                    Boolean hasPlacedBlockCount = false;
                    Boolean hasRemovedBlockCount = false;
                    Boolean hasXyz = false;
                    Boolean hasXyzremove = false;

                    while (result != null && result.next()) {
                        String key = "";
                        key = result.getString("Key_name");
                        if ("placedBlockCount".equals(key)) {
                            hasPlacedBlockCount = true;
                        }
                        if ("removedBlockCount".equals(key)) {
                            hasRemovedBlockCount = true;
                        }
                        if ("xyz".equals(key)) {
                            hasXyz = true;
                        }
                        if ("xyzremove".equals(key)) {
                            hasXyzremove = true;
                        }

                    }

                    if (!hasPlacedBlockCount || !hasRemovedBlockCount || !hasXyz || !hasXyzremove) {
                        this.log.info("[WhoPlacedIt] Updating Database indexes (might take a bit of time if table is large)");

                        query = "ALTER TABLE `trackedBlocks` ";
                        queryExt = "";

                        if (!hasPlacedBlockCount) {
                            queryExt += "ADD INDEX `placedBlockCount` (`createTime`,`createPlayerUUID`)";
                        }
                        if (!hasRemovedBlockCount) {
                            if (!"".equals(queryExt)) {
                                queryExt += ", ";
                            }
                            queryExt += "ADD INDEX `removedBlockCount` (`removeTime`,`removePlayerUUID`)";
                        }
                        if (!hasXyz) {
                            if (!"".equals(queryExt)) {
                                queryExt += ", ";
                            }
                            queryExt += "ADD INDEX `xyz` (`x` ,`y` ,`z`)";
                        }
                        if (!hasXyzremove) {
                            if (!"".equals(queryExt)) {
                                queryExt += ", ";
                            }
                            queryExt += "ADD INDEX `xyzremove` ( `x` , `y` , `z` , `removeTime` )";
                        }
                        if (!"".equals(queryExt)) { //added extra check..
                            this.manageMySQL.updateQuery(query + queryExt);
                        }
                    }
                    this.log.info("[WhoPlacedIt] Database check complete.");

                } else {
                    this.log.severe("[WhoPlacedIt] MySQL connection failed, falling back to sqlite");
                    this.mode = 2;
                }
            } catch (NullPointerException e) {
                log.severe("[WhoPlacedIt] Could not establish connection to mysql server, falling back to sqlite");
                this.mode = 2;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                log.info("[WhoPlacedIt] Error, something went wrong with the sql query");
            }
        }

        //TODO: optimize SQLite
        if (this.mode == 2) {
            this.log.info("[WhoPlacedIt] SQLite Initializing");

            // Declare SQLite handler
            this.manageSQLite = new sqlCore(this.log, "[SQL INFO]", "WhoPlacedIt", directory.getPath());

            // Initialize SQLite handler
            this.manageSQLite.initialize();

            // Check if the table exists, if it doesn't create it
            if (!this.manageSQLite.checkTable("trackedBlocks")) {
                this.log.info("[WhoPlacedIt] Creating table trackedBlocks");
                query = "CREATE TABLE trackedBlocks (id INT AUTO_INCREMENT PRIMARY_KEY, createPlayer VARCHAR(255), createPlayerUUID VARCHAR(255), removePlayer VARCHAR(255), removePlayerUUID VARCHAR(255), x INT, y INT, z INT, createTime BIGINT, removeTime BIGINT, cause VARCHAR(255) NOT NULL DEFAULT 'player');";
                this.manageSQLite.createTable(query);
            }

            query = "SELECT * FROM trackedBlocks LIMIT 0,1;";
            result = null;
            result = this.manageSQLite.sqlQuery(query);
            ResultSetMetaData resultMetaData;
            try {
                resultMetaData = result.getMetaData();
                int columnCount = resultMetaData.getColumnCount();
                boolean cause = false;
                boolean blockTypeID = false;
                for (int i = 1; i < columnCount + 1; i++) {
                    if (resultMetaData.getColumnName(i).equals("cause")) {
                        cause = true;
                    }
                    if (resultMetaData.getColumnName(i).equals("blockTypeID")) {
                        blockTypeID = true;
                    }
                    if (cause == true && blockTypeID == true) {
                        break;
                    }
                }
                if (!cause) {
                    this.log.info("[WhoPlacedIt] Missing Database field cause, adding it");
                    query = "ALTER TABLE trackedBlocks ADD cause VARCHAR(255) NOT NULL DEFAULT 'player';";
                    this.manageSQLite.updateQuery(query);
                }
                if (!blockTypeID) {
                    this.log.info("[WhoPlacedIt] Missing Database field blockTypeID, adding it");
                    query = "ALTER TABLE trackedBlocks ADD blockTypeID INT NOT NULL DEFAULT -1;";
                    this.manageSQLite.updateQuery(query);
                }
            } catch (SQLException e) {
                log.info("[WhoPlacedIt] Error, something went wrong with the sql query");
            }
        }
    }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    private void loadProperties() {
        // Read properties file.
        try {
            properties.load(new FileInputStream(fileName));
        } catch (IOException e) {
            // store default values
            log.info("[WhoPlacedIt] Error, found no properties file, creating one with default values");
            properties.setProperty("database", "sqlite");
            properties.setProperty("triggerItem", "280");
            properties.setProperty("dateFormat", "yyyy-MM-dd HH:mm:ss");
            properties.setProperty("enableHistory", "true");
            properties.setProperty("historyEntries", "3");
            properties.setProperty("enableStats", "true");
            properties.setProperty("mysqlHost", "localhost");
            properties.setProperty("mysqlUser", "");
            properties.setProperty("mysqlPassword", "");
            properties.setProperty("mysqlDatabase", "whoplacedit");
            // Write properties file.
            try {
                properties.store(new FileOutputStream(fileName), null);
            } catch (IOException ex) {
                log.info("[WhoPlacedIt] Error, could not write properties file");
            }
        }
        if (properties.getProperty("enableHistory") == null) {
            properties.setProperty("enableHistory", "true");
        }
        if (properties.getProperty("enableStats") == null) {
            properties.setProperty("enableStats", "true");
        }
        if (this.properties.getProperty("dateFormat") == null) {
            properties.setProperty("dateFormat", "yyyy-MM-dd HH:mm:ss");
        }
        if (this.properties.getProperty("historyEntries") == null) {
            properties.setProperty("historyEntries", "3");
        }
    }

    /**
     * @return the inHand
     */
    public ArrayList<Integer> getInHand() {
        if (inHand.isEmpty()) {
            String triggerItem = this.properties.getProperty("triggerItem");
            for (String split : triggerItem.split(",")) {
                try {
                    this.inHand.add(Integer.valueOf(split.trim()));
                } catch (NumberFormatException e) {
                    // silently fail
                }
            }
        }
        return inHand;
    }

    /**
     * writes the information into the database that a block has been placed by
     * the given player at the given time
     *
     * @param block
     * @param player
     * @param createTime
     */
    public void placeBlock(Block block, Player player, long createTime) {
        String query;

        if (this.mode == 1) {
            query = "INSERT DELAYED INTO trackedBlocks (createPlayer, createPlayerUUID, removePlayer, removePlayerUUID, x, y, z, createTime, removeTime, blockTypeID) VALUES ('"
                    + player.getName()
                    + "','"
                    + player.getUniqueId().toString()
                    + "', '','', "
                    + block.getX()
                    + ", "
                    + block.getY() + ", " + block.getZ() + ", " + createTime + ", " + 0 + ", " + block.getTypeId() + ");";

            try {
                this.manageMySQL.insertQuery(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (this.mode == 2) {
            query = "INSERT INTO trackedBlocks (createPlayer, createPlayerUUID, removePlayer, removePlayerUUID, x, y, z, createTime, removeTime, blockTypeID) VALUES ('"
                    + player.getName()
                    + "','"
                    + player.getUniqueId().toString()
                    + "', '','', "
                    + block.getX()
                    + ", "
                    + block.getY() + ", " + block.getZ() + ", " + createTime + ", " + 0 + ", " + block.getTypeId() + ");";
            this.manageSQLite.insertQuery(query);
        }
    }

    /**
     * Sets the remove time of the given block
     *
     * @param block
     * @param removeTime
     */
    public void removeBlock(Block block, Player player, long removeTime) {
        String query;
        query = "UPDATE trackedBlocks SET removeTime = " + removeTime + ", removePlayer = '" + player.getName()
                + "', removePlayerUUID = '" + player.getUniqueId().toString() + "', blockTypeID = " + block.getTypeId()
                + " WHERE x = " + block.getX() + " AND y = " + block.getY() + " AND z = " + block.getZ()
                + " AND removeTime = 0;";
        if (this.mode == 1) {
            try {
                this.manageMySQL.updateQuery(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (this.mode == 2) {
            this.manageSQLite.updateQuery(query);
        }
    }

    public void burnBlock(Block block, long removeTime) {
        String query;
        query = "SELECT * FROM trackedBlocks WHERE x = " + block.getX() + " AND y = " + block.getY()
                + " AND z = " + block.getZ() + " AND removeTime = 0 LIMIT 1";
        ResultSet result = null;

        if (this.mode == 1) {
            try {
                result = this.manageMySQL.sqlQuery(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (this.mode == 2) {
            result = this.manageSQLite.sqlQuery(query);
        }

        try {
            if (result != null && result.next()) {
                query = "UPDATE trackedBlocks SET removeTime = " + removeTime + ", cause = 'fire', blockTypeID = "
                        + block.getTypeId() + " WHERE x = " + block.getX() + " AND y = " + block.getY() + " AND z = "
                        + block.getZ() + " AND removeTime = 0;";
            } else {
                query = "INSERT INTO trackedBlocks (createPlayer, createPlayerUUID, removePlayer, removePlayerUUID, x, y, z, createTime, removeTime, cause,blockTypeID) VALUES ("
                        + "'',"
                        + "'',"
                        + "'',"
                        + "'',"
                        + block.getX()
                        + ", "
                        + block.getY()
                        + ", "
                        + block.getZ()
                        + ", " + removeTime + ", " + removeTime + ", 'fire'," + block.getTypeId() + ");";

            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (this.mode == 1) {
            try {
                this.manageMySQL.updateQuery(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (this.mode == 2) {
            this.manageSQLite.updateQuery(query);
        }
    }

    /**
     * Returns information about the given block in the following format: <br />
     * USERNAME created on yyyy-MM-dd HH:mm:ss deleted on yyyy-MM-dd HH:mm:ss
     *
     * @param block
     * @return
     */
    public ArrayList<BlockInfo> getBlockInfo(Block block, Player player) {
        String query;
        query = "SELECT * FROM trackedBlocks WHERE x = " + block.getX() + " AND y = " + block.getY()
                + " AND z = " + block.getZ() + " ORDER BY createTime DESC LIMIT ";

        if (properties.getProperty("enableHistory").equals("true")) {
            query += this.properties.getProperty("historyEntries") + ";";
        } else {
            query += "1;";
        }
        ResultSet result = null;
        ArrayList<BlockInfo> user = new ArrayList<BlockInfo>();

        if (this.mode == 1) {
            try {
                result = this.manageMySQL.sqlQuery(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (this.mode == 2) {
            result = this.manageSQLite.sqlQuery(query);
        }
        try {
            while (result != null && result.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat(this.properties.getProperty("dateFormat"));

                Date resultCreateDate = new Date(result.getLong("createTime"));
                Date resultRemoveDate = new Date(result.getLong("removeTime"));

                int blockTypeID = Integer.parseInt(result.getString("blockTypeID"));
                String blockName = "unknown";
                String prefix;
                if (blockTypeID >= 0) {
                    blockName = Material.getMaterial(blockTypeID).toString().toLowerCase().replace("_", " ");
                }

                if (blockName.startsWith("a") || blockName.startsWith("e") || blockName.startsWith("i")
                        || blockName.startsWith("o") || blockName.startsWith("u")) {
                    prefix = "an";
                } else {
                    prefix = "a";
                }

                BlockInfo createPlayer = null;
                if (result.getString("createPlayerUUID").equals(player.getUniqueId().toString())) {
                    createPlayer = new BlockInfo(ChatColor.GREEN, "You placed " + prefix + " " + blockName
                            + " block on " + sdf.format(resultCreateDate), resultCreateDate);
                } else if (!result.getString("createPlayerUUID").equals("")) {
                    createPlayer = new BlockInfo(ChatColor.YELLOW, result.getString("createPlayer") + " placed "
                            + prefix + " " + blockName + " block on " + sdf.format(resultCreateDate), resultCreateDate);
                }
                BlockInfo removePlayer = null;

                if (!result.getString("removePlayer").isEmpty()) {
                    if (result.getString("removePlayerUUID").equals(player.getUniqueId().toString())) {
                        removePlayer = new BlockInfo(ChatColor.GREEN, "You removed " + prefix + " " + blockName
                                + " block on " + sdf.format(resultRemoveDate), resultRemoveDate);
                    } else {
                        removePlayer = new BlockInfo(ChatColor.YELLOW, result.getString("removePlayer") + " removed "
                                + prefix + " " + blockName + " block on " + sdf.format(resultRemoveDate),
                                resultRemoveDate);
                    }
                } else if (result.getString("cause").equals("fire")) {
                    removePlayer = new BlockInfo(ChatColor.RED, "Fire burnt " + prefix + " " + blockName + " block on "
                            + sdf.format(resultRemoveDate), resultRemoveDate);
                    ;

                }
                if (createPlayer != null) {
                    user.add(createPlayer);
                }
                if (removePlayer != null) {
                    user.add(removePlayer);
                }
            }
        } catch (SQLException e) {
            log.info("[WhoPlacedIt] Error, something went wrong with the sql query");
        }
        Collections.sort(user);

        return user;
    }

    public int getPlacedBlockCount(Player player) {
        String query;
        ResultSet result = null;

        if (this.mode == 1) {
            query = "SELECT COUNT(*) AS `rowcount` FROM `trackedBlocks` WHERE `createTime` != '' AND `createPlayerUUID` = '"
                    + player.getUniqueId().toString() + "'";

            try {
                result = this.manageMySQL.sqlQuery(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (this.mode == 2) {
            query = "SELECT COUNT(*) AS rowcount FROM trackedBlocks WHERE createTime != '' AND createPlayerUUID = '"
                    + player.getUniqueId().toString() + "'";

            result = this.manageSQLite.sqlQuery(query);
        }
        try {
            while (result != null && result.next()) {
                return result.getInt("rowcount");
            }
        } catch (SQLException e) {
            log.info("[WhoPlacedIt] Error, something went wrong with the sql query");
        }
        return 0;
    }

    public int getRemovedBlockCount(Player player) {
        String query;
        ResultSet result = null;


        if (this.mode == 1) {
            query = "SELECT COUNT(*) AS `rowcount` FROM `trackedBlocks` WHERE `removeTime` != '' AND `removePlayerUUID` = '"
                    + player.getUniqueId().toString() + "'";
            try {
                result = this.manageMySQL.sqlQuery(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } else if (this.mode == 2) {
            query = "SELECT COUNT(*) AS rowcount FROM trackedBlocks WHERE removeTime != '' AND removePlayerUUID = '"
                    + player.getUniqueId().toString() + "'";
            result = this.manageSQLite.sqlQuery(query);
        }
        try {
            while (result != null && result.next()) {
                return result.getInt("rowcount");
            }
        } catch (SQLException e) {
            log.info("[WhoPlacedIt] Error, something went wrong with the sql query");
        }
        return 0;
    }
}
