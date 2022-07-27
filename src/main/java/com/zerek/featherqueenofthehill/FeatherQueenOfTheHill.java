package com.zerek.featherqueenofthehill;

import com.zerek.featherqueenofthehill.commands.QueenCommand;
import com.zerek.featherqueenofthehill.commands.QueenTabCompleter;
import com.zerek.featherqueenofthehill.listeners.EntityDamageByEntityListener;
import com.zerek.featherqueenofthehill.listeners.PlayerLoginListener;
import com.zerek.featherqueenofthehill.managers.QueenManager;
import com.zerek.featherqueenofthehill.tasks.InitiateTask;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
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
        this.getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this),this);
        this.getServer().getPluginManager().registerEvents(new PlayerLoginListener(this),this);

        this.getLogger().info("Attempting to start QueenOfTheHill.");
        getServer().getScheduler().runTaskLater(this, new InitiateTask(this),200L);

        this.getCommand("queen").setExecutor(new QueenCommand(this));
        this.getCommand("queen").setTabCompleter(new QueenTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload(CommandSender sender){
        getServer().getScheduler().cancelTasks(this);
        this.reloadConfig();
        queenManager = new QueenManager(this);
        this.getLogger().info("Attempting to start QueenOfTheHill.");
        getServer().getScheduler().runTask(this, new InitiateTask(this));
        sender.sendMessage("QueenOfTheHill reloaded");
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

