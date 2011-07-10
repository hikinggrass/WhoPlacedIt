package de.hikinggrass.WhoPlacedIt;

import java.util.logging.Logger;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WhoPlacedItBlockListener extends BlockListener {
	public static WhoPlacedIt plugin;
	protected Logger log;
	protected Management manager;

	public WhoPlacedItBlockListener(WhoPlacedIt instance, Logger log, Management manager) {
		plugin = instance;
		this.log = log;
		this.manager = manager;
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		/*
		 * this.log.info("Player " + event.getPlayer().getDisplayName() + " placed block.");
		 * 
		 * log.info("blockplaced is located at: x:" + event.getBlockPlaced().getX() + " y: " +
		 * event.getBlockPlaced().getY() + " z: " + event.getBlockPlaced().getZ()); log.info("block is located at: x:" +
		 * event.getBlock().getX() + " y: " + event.getBlock().getY() + " z: " + event.getBlock().getZ());
		 */
		manager.placeBlock(event.getBlockPlaced(), event.getPlayer());
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		manager.removeBlock(event.getBlock(), event.getPlayer());
	}
}