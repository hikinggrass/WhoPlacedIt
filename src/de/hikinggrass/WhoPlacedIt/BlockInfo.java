package de.hikinggrass.WhoPlacedIt;

import org.bukkit.ChatColor;

public class BlockInfo {
	protected ChatColor color;
	protected String message;
	/**
	 * @param color
	 * @param message
	 */
	public BlockInfo(ChatColor color, String message) {
		super();
		this.color = color;
		this.message = message;
	}
	/**
	 * @return the color
	 */
	public ChatColor getColor() {
		return color;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(ChatColor color) {
		this.color = color;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
