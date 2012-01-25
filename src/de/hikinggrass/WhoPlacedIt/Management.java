package de.hikinggrass.WhoPlacedIt;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Management {

    protected Logger log;
    protected Storage store;
    protected int mode;

    /**
     *
     */
    public Management(Logger log) {
        super();
        this.store = new Storage(log);
        this.mode = this.store.getMode();
    }

    public void placeBlock(Block block, Player player) {
        this.store.placeBlock(block, player, System.currentTimeMillis());
    }

    public void removeBlock(Block block, Player player) {
        this.store.removeBlock(block, player, System.currentTimeMillis());
    }

    public void burnBlock(Block block) {
        this.store.burnBlock(block, System.currentTimeMillis());
    }

    public ArrayList<BlockInfo> getBlockInfo(Block block, Player player) {
        return this.store.getBlockInfo(block, player);
    }

    public ArrayList<Integer> getInHand() {
        return this.store.getInHand();
    }

    public int getPlacedBlockCount(Player player) {
        return this.store.getPlacedBlockCount(player);
    }

    public int getRemovedBlockCount(Player player) {
        return this.store.getRemovedBlockCount(player);
    }
}
