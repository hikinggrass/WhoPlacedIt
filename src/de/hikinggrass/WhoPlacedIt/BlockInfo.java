package de.hikinggrass.WhoPlacedIt;

import java.util.Date;

import org.bukkit.ChatColor;

public class BlockInfo implements Comparable<BlockInfo> {

    protected ChatColor color;
    protected String message;
    protected Date time;

    /**
     * @param color
     * @param message
     */
    public BlockInfo(ChatColor color, String message, Date time) {
        super();
        this.color = color;
        this.message = message;
        this.time = time;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
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

    @Override
    public int compareTo(BlockInfo arg0) {
        return this.time.compareTo(arg0.getTime());
    }
}
