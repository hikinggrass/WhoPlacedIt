package de.hikinggrass.WhoPlacedIt;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Management {

	protected Logger log;
	Storage store;
	ArrayList<TrackedBlock> trackedBlocks;

	/**
	 * 
	 */
	public Management(Logger log) {
		super();
		this.store = new Storage(log);
		this.trackedBlocks = store.load();
		if (trackedBlocks == null) {
			trackedBlocks = new ArrayList<TrackedBlock>();
		}
		// TODO remove this line?
		store.save(trackedBlocks);

	}

	public void placeBlock(Block block, Player player) {
		// if a block is already at this position - remove it
		if (this.getBlockInfo(block) != null) {
			this.removeBlock(block);
		}
		this.trackedBlocks.add(new TrackedBlock(block, player));
		this.store.save(trackedBlocks);
	}

	public void removeBlock(Block block) {
		int i = 0;
		for (TrackedBlock tracked : this.trackedBlocks) {
			if (tracked.getBlockLocationX() == block.getX() && tracked.getBlockLocationY() == block.getY()
					&& tracked.getBlockLocationZ() == block.getZ()) {
				this.trackedBlocks.remove(i);
				break;
			}
			i++;
		}
	}

	public String getBlockInfo(Block block) {
		for (TrackedBlock tracked : this.trackedBlocks) {
			if (tracked.getBlockLocationX() == block.getX() && tracked.getBlockLocationY() == block.getY()
					&& tracked.getBlockLocationZ() == block.getZ()) {
				return tracked.getPlayerName();
			}
		}
		return null;
	}

	/**
	 * Stores the TrackedBlocks in File/DB
	 */
	public void saveTrackedBlocks() {
		this.store.save(trackedBlocks);
	}
}
