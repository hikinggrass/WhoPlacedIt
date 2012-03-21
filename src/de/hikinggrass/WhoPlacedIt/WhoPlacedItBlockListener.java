package de.hikinggrass.WhoPlacedIt;

import java.util.logging.Logger;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
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
        manager.placeBlock(event.getBlockPlaced(), event.getPlayer());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        manager.removeBlock(event.getBlock(), event.getPlayer());
    }

    @Override
    public void onBlockBurn(BlockBurnEvent event) {
        manager.burnBlock(event.getBlock());
    }
}