package com.zerek.featherqueenofthehill;

import com.zerek.featherqueenofthehill.commands.QueenOfTheHillCommand;
import com.zerek.featherqueenofthehill.commands.QueenOfTheHillTabCompleter;
import com.zerek.featherqueenofthehill.managers.QueenManager;
import com.zerek.featherqueenofthehill.tasks.InitiateTask;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;


public final class FeatherQueenOfTheHill extends JavaPlugin {

    private ArmorStand stand = null;
    private Sign sign = null;
    private QueenManager queenManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        queenManager = new QueenManager(this);
        getServer().getScheduler().runTaskLater(this, new InitiateTask(this),800);
        this.getCommand("queenofthehill").setExecutor(new QueenOfTheHillCommand(this));
        this.getCommand("queenofthehill").setTabCompleter(new QueenOfTheHillTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload(){
        getServer().getScheduler().cancelTasks(this);
        this.reloadConfig();
        queenManager = new QueenManager(this);
        getServer().getScheduler().runTask(this, new InitiateTask(this));
    }

    public QueenManager getQueenManager() {
        return queenManager;
    }

    public ArmorStand getStand() {
        return stand;
    }
    public void setStand(ArmorStand stand) {
        this.stand = stand;
    }

    public Sign getSign() {
        return sign;
    }
    public void setSign(Sign sign) {
        this.sign = sign;
    }
}

