package de.hikinggrass.WhoPlacedIt;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Management {

	protected Logger log;
	Storage store;

	protected int mode = 2;

	/**
	 * 
	 */
	public Management(Logger log) {
		super();
		this.store = new Storage(log, mode);
	}

	public void placeBlock(Block block, Player player) {
		if (this.getBlockInfo(block) != null) {
			this.removeBlock(block);
		}

		if (this.mode == 2) {
			this.store.placeBlock(block, player, System.currentTimeMillis());
		}
		
	}

	public void removeBlock(Block block) {
		if (this.mode == 2) {
			this.store.removeBlock(block, System.currentTimeMillis());
		}
	}

	public ArrayList<String> getBlockInfo(Block block) {
		if (this.mode == 2) {
			return this.store.getBlockInfo(block);
		}

		return null;
	}
}
