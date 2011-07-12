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
		/*
		 * if (this.getBlockInfo(block) != null) { this.removeBlock(block); }
		 */

		if (this.mode == 2) {
			this.store.placeBlock(block, player, System.currentTimeMillis());
		}

	}

	public void removeBlock(Block block, Player player) {
		if (this.mode == 2) {
			this.store.removeBlock(block, player, System.currentTimeMillis());
		}
	}

	public ArrayList<BlockInfo> getBlockInfo(Block block, Player player) {
		if (this.mode == 2) {
			return this.store.getBlockInfo(block, player);
		}

		return null;
	}

	public ArrayList<Integer> getInHand() {
		return this.store.getInHand();
	}
}
