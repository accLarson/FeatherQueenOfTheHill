package com.zerek.featherqueenofthehill.tasks;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

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
    }


    @Override
    public void run() {
        Collection<Player> players = stand.getLocation().getNearbyPlayers(range,range,range);
        plugin.getQueenManager().updateGame(players, stand, sign);
    }
}
