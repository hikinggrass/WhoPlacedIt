package de.hikinggrass.WhoPlacedIt;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WhoPlacedIt extends JavaPlugin {
	protected static final String version = "0.2";

	protected Logger log = Logger.getLogger("Minecraft");

	protected Management manager = new Management(log);

	public void onEnable() {
		log.info("[WhoPlacedIt] WhoPlacedIt " + version + " enabled!");

		PluginManager pm = this.getServer().getPluginManager();

		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);

		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);

	}

	public void onDisable() {
		log.info("[WhoPlacedIt] WhoPlacedIt " + version + " disabled!");
	}

	private final WhoPlacedItPlayerListener playerListener = new WhoPlacedItPlayerListener(this, log, manager);
	private final WhoPlacedItBlockListener blockListener = new WhoPlacedItBlockListener(this, log, manager);
}
