package de.hikinggrass.WhoPlacedIt;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class WhoPlacedItPlayerListener implements Listener {

    public static WhoPlacedIt plugin;
    protected Logger log;
    protected Management manager;

    public WhoPlacedItPlayerListener(WhoPlacedIt instance, Logger log, Management manager) {
        plugin = instance;
        this.log = log;
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getPlayer().hasPermission("whoplacedit.magicstick")) {
                if ((this.manager.getInHand().isEmpty() || this.manager.getInHand().contains(
                        event.getPlayer().getItemInHand().getTypeId()))) {
                    for (BlockInfo name : this.manager.getBlockInfo(event.getClickedBlock(), event.getPlayer())) {
                        if (name != null) {
                            for (String line : name.getMessage().split("\n")) {
                                event.getPlayer().sendMessage(name.getColor() + line);
                            }
                        }
                    }
                }
            }
        }
    }
}