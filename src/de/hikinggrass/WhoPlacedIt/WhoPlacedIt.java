package de.hikinggrass.WhoPlacedIt;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WhoPlacedIt extends JavaPlugin {
	protected static final String version = "0.4.1";

	protected Logger log = Logger.getLogger("Minecraft");

	protected Management manager = new Management(log);

	public void onEnable() {
		log.info("[WhoPlacedIt] WhoPlacedIt " + version + " enabled!");

		PluginManager pm = this.getServer().getPluginManager();

		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Normal, this);

		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);

	}

	public void onDisable() {
		log.info("[WhoPlacedIt] WhoPlacedIt " + version + " disabled!");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (cmd.getName().equalsIgnoreCase("wpistats")) { // If the player typed /basic then do the following...
			sender.sendMessage(ChatColor.BLUE + "Placed Blocks: " + manager.getPlacedBlockCount((Player) sender));
			sender.sendMessage(ChatColor.BLUE + "Removed Blocks: " + manager.getRemovedBlockCount((Player) sender));
			return true;
		} // If this has happened the function will break and return true. if this hasn't happened the a value of false
		  // will be returned.
		return false;
	}

	private final WhoPlacedItPlayerListener playerListener = new WhoPlacedItPlayerListener(this, log, manager);
	private final WhoPlacedItBlockListener blockListener = new WhoPlacedItBlockListener(this, log, manager);
}
