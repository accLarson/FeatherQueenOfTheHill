package dev.zerek.featherqueenofthehill;

import dev.zerek.featherqueenofthehill.commands.QueenCommand;
import dev.zerek.featherqueenofthehill.commands.QueenTabCompleter;
import dev.zerek.featherqueenofthehill.listeners.EntityDamageByEntityListener;
import dev.zerek.featherqueenofthehill.listeners.PlayerLoginListener;
import dev.zerek.featherqueenofthehill.managers.DatabaseManager;
import dev.zerek.featherqueenofthehill.managers.ScoreManager;
import dev.zerek.featherqueenofthehill.managers.QueenManager;
import dev.zerek.featherqueenofthehill.tasks.InitiateTask;
import dev.zerek.featherqueenofthehill.utilities.ChatUtility;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;


public final class FeatherQueenOfTheHill extends JavaPlugin {

    private DatabaseManager databaseManager;

    private ArmorStand stand = null;
    private Sign sign = null;
    private QueenManager queenManager;
    private ScoreManager scoreManager;
    private ChatUtility chatUtility;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        queenManager = new QueenManager(this);
        scoreManager = new ScoreManager(this);
        chatUtility = new ChatUtility(this);

        this.getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this),this);
        this.getServer().getPluginManager().registerEvents(new PlayerLoginListener(this),this);

        this.getLogger().info("Attempting to start QueenOfTheHill.");
        getServer().getScheduler().runTaskLater(this, new InitiateTask(this),200L);

        this.getCommand("queen").setExecutor(new QueenCommand(this));
        this.getCommand("queen").setTabCompleter(new QueenTabCompleter());
    }

    @Override
    public void onDisable() {
        this.scoreManager.updateDatabase();
    }

    public void reload(CommandSender sender){
        this.scoreManager.updateDatabase();
        getServer().getScheduler().cancelTasks(this);
        this.reloadConfig();

        databaseManager = new DatabaseManager(this);
        queenManager = new QueenManager(this);
        scoreManager = new ScoreManager(this);
        chatUtility = new ChatUtility(this);

        this.getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this),this);
        this.getServer().getPluginManager().registerEvents(new PlayerLoginListener(this),this);

        this.getLogger().info("Attempting to start QueenOfTheHill.");
        getServer().getScheduler().runTask(this, new InitiateTask(this));

        sender.sendMessage("QueenOfTheHill reloaded");
    }

    public QueenManager getQueenManager() {
        return queenManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public ChatUtility getChatUtility() {
        return chatUtility;
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

