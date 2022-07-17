package com.zerek.featherqueenofthehill.tasks;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class InitiateTask implements Runnable{

    private final FeatherQueenOfTheHill plugin;

    public InitiateTask(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getLogger().info("Attempting to start QueenOfTheHill.");

        List<Integer> standConfig = this.plugin.getConfig().getIntegerList("stand");
        List<Integer> signConfig = this.plugin.getConfig().getIntegerList("sign");
        int range = this.plugin.getConfig().getInt("range");
        World world = plugin.getServer().getWorlds().get(0);
        Location standLoc = new Location(world, standConfig.get(0), standConfig.get(1), standConfig.get(2));
        Location signLoc = new Location(world, signConfig.get(0), signConfig.get(1), signConfig.get(2));
        String queenName = null;

        standLoc.getChunk().load();

        List<ArmorStand> standList = (List<ArmorStand>) standLoc.toCenterLocation().getNearbyEntitiesByType(ArmorStand.class, 0.5,1.0,0.5);

        if (standList.size() > 0) {
            plugin.getLogger().info("Armor stand found.");
            plugin.setStand(standList.get(0));
            if (plugin.getStand().getEquipment().getHelmet().getType() == Material.PLAYER_HEAD){
                SkullMeta skullMeta = (SkullMeta) plugin.getStand().getEquipment().getHelmet().getItemMeta();
                plugin.getQueenManager().setQueen(skullMeta.getOwningPlayer());
                queenName = skullMeta.getOwningPlayer().getName();
            }
        } else plugin.getLogger().warning("No armor stand found at: " + standLoc.getBlockX() + " " + standLoc.getBlockY() + " " + standLoc.getBlockZ() + ".");

        if (signLoc.getBlock().getState() instanceof Sign) {
            plugin.getLogger().info("Sign found.");
            plugin.setSign((Sign) signLoc.getBlock().getState());
        } else plugin.getLogger().warning("No sign found at: " + signLoc.getBlockX() + " " + signLoc.getBlockY() + " " + signLoc.getBlockZ() + ".");


        if (queenName != null) plugin.getLogger().info(queenName + " has been set as the Queen.");

        if (plugin.getStand() == null || plugin.getSign() == null) {
            this.plugin.getLogger().info("Failed to start QueenOfTheHill. Trying again in 1 minute");
            plugin.getServer().getScheduler().runTaskLater(plugin, new InitiateTask(plugin),1200);
        }
        else plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new PlayerCheckTask(plugin,plugin.getStand(),plugin.getSign(), range), 0L, 20L);
    }
}
