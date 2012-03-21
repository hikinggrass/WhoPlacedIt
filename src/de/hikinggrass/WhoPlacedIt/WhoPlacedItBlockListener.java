package de.hikinggrass.WhoPlacedIt;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class WhoPlacedItBlockListener implements Listener {

    public static WhoPlacedIt plugin;
    protected Logger log;
    protected Management manager;

    public WhoPlacedItBlockListener(WhoPlacedIt instance, Logger log, Management manager) {
        plugin = instance;
        this.log = log;
        this.manager = manager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        manager.placeBlock(event.getBlockPlaced(), event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        manager.removeBlock(event.getBlock(), event.getPlayer());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        manager.burnBlock(event.getBlock());
    }
}