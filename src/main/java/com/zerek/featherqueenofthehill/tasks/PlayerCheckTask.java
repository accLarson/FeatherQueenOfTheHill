package com.zerek.featherqueenofthehill.tasks;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.Collection;

public class PlayerCheckTask implements Runnable{
    private final FeatherQueenOfTheHill plugin;
    private final ArmorStand stand;
    private final Sign sign;
    private final double range;

    public PlayerCheckTask(FeatherQueenOfTheHill plugin, ArmorStand stand, Sign sign, double range) {
        this.plugin = plugin;
        this.stand = stand;
        this.sign = sign;
        this.range = range;
        this.plugin.getLogger().info("Successfully started QueenOfTheHill.");
    }


    @Override
    public void run() {
        Collection<Player> players = stand.getLocation().getNearbyPlayers(range,range,range);
        players.removeIf(this::isVanished);
        plugin.getQueenManager().updateGame(players, stand, sign);
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
