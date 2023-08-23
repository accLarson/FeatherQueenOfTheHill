package dev.zerek.featherqueenofthehill.listeners;

import dev.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {
    private final FeatherQueenOfTheHill plugin;

    public PlayerLoginListener(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        if (this.plugin.getQueenManager().isQueen(event.getPlayer())) plugin.getQueenManager().setQueen(event.getPlayer());
    }
}
