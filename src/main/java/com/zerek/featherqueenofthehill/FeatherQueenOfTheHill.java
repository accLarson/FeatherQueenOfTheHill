package com.zerek.featherqueenofthehill;

import com.zerek.featherqueenofthehill.managers.QueenManager;
import com.zerek.featherqueenofthehill.tasks.InitiateTask;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;


public final class FeatherQueenOfTheHill extends JavaPlugin {

    private ArmorStand stand;
    private Sign sign;
    private QueenManager queenManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        queenManager = new QueenManager(this);
        getServer().getScheduler().runTaskLater(this, new InitiateTask(this),1200);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public QueenManager getQueenManager() {
        return queenManager;
    }
    public ArmorStand getStand() {
        return stand;
    }
    public Sign getSign() {
        return sign;
    }
    public void setStand(ArmorStand stand) {
        this.stand = stand;
    }
    public void setSign(Sign sign) {
        this.sign = sign;
    }
}

