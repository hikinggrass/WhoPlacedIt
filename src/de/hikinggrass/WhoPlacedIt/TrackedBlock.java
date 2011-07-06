package de.hikinggrass.WhoPlacedIt;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TrackedBlock implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6498597833717321871L;
	protected int blockTypeId;
	protected int blockLocationX;
	protected int blockLocationY;
	protected int blockLocationZ;
	protected UUID uuid;
	protected String playerName;

	/**
	 * 
	 */
	public TrackedBlock(Block block, Player player) {
		super();
		this.blockTypeId = block.getTypeId();
		this.blockLocationX = block.getX();
		this.blockLocationY = block.getY();
		this.blockLocationZ = block.getZ();
		this.uuid = player.getUniqueId();
		this.playerName = player.getDisplayName();
	}

	/**
	 * @return the blockTypeId
	 */
	public int getBlockTypeId() {
		return blockTypeId;
	}

	/**
	 * @return the blockLocationX
	 */
	public int getBlockLocationX() {
		return blockLocationX;
	}

	/**
	 * @return the blockLocationY
	 */
	public int getBlockLocationY() {
		return blockLocationY;
	}

	/**
	 * @return the blockLocationZ
	 */
	public int getBlockLocationZ() {
		return blockLocationZ;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrackedBlock [blockTypeId=" + blockTypeId + ", blockLocationX=" + blockLocationX + ", blockLocationY="
				+ blockLocationY + ", blockLocationZ=" + blockLocationZ + ", uuid=" + uuid + ", playerName="
				+ playerName + "]";
	}

}
