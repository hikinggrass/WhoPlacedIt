package de.hikinggrass.WhoPlacedIt;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Management {

	protected Logger log;
	Storage store;
	ArrayList<TrackedBlock> trackedBlocks;

	protected int mode = 2;

	/**
	 * 
	 */
	public Management(Logger log) {
		super();
		this.store = new Storage(log, mode);
		this.trackedBlocks = store.load();
		if (trackedBlocks == null) {
			trackedBlocks = new ArrayList<TrackedBlock>();
		}
		// TODO remove this line?
		store.save(trackedBlocks);

	}

	public void placeBlock(Block block, Player player) {
		if (this.getBlockInfo(block) != null) {
			this.removeBlock(block);
		}
		
		if (this.mode == 2) {
			this.store.placeBlock(block, player);
		} else {
			// if a block is already at this position - remove it
			
			this.trackedBlocks.add(new TrackedBlock(block, player));
			this.store.save(trackedBlocks);
		}
	}

	public void removeBlock(Block block) {
		if (this.mode == 2) {
			this.store.removeBlock(block);
		} else {
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
	}

	public String getBlockInfo(Block block) {
		if (this.mode == 2) {
			return this.store.getBlockInfo(block);
		} else {
			for (TrackedBlock tracked : this.trackedBlocks) {
				if (tracked.getBlockLocationX() == block.getX() && tracked.getBlockLocationY() == block.getY()
						&& tracked.getBlockLocationZ() == block.getZ()) {
					return tracked.getPlayerName();
				}
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
