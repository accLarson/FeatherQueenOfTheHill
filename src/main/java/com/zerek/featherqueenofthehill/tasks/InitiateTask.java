package com.zerek.featherqueenofthehill.tasks;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.stream.Collectors;

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

        World world = plugin.getServer().getWorlds().get(0);
        Location standLoc = new Location(world, standConfig.get(0), standConfig.get(1), standConfig.get(2));
        Location signLoc = new Location(world, signConfig.get(0), signConfig.get(1), signConfig.get(2));

        int minutes;
        double score;

        // Check if stand is loaded.
        if (standLoc.getChunk().isEntitiesLoaded()){

            // Check for armor stand at specified location and set stand if found.
            List<ArmorStand> standList = (List<ArmorStand>) standLoc.toCenterLocation().getNearbyEntitiesByType(ArmorStand.class, 0.5,1.0,0.5);
            if (standList.size() > 0) {
                plugin.getLogger().info("Armor stand found.");
                plugin.setStand(standList.get(0));
            }
            // No stand found.
            else plugin.getLogger().warning("No armor stand found at: " + standLoc.getBlockX() + " " + standLoc.getBlockY() + " " + standLoc.getBlockZ() + ".");


            // Check if sign was found at specified location in config. If true, set the sign.
            if (signLoc.getBlock().getState() instanceof Sign) {
                plugin.getLogger().info("Sign found.");
                plugin.setSign((Sign) signLoc.getBlock().getState());

                // Parse queen from sign and set queen.
                String nameLine = PlainTextComponentSerializer.plainText().serialize(plugin.getSign().line(0));
                if (!nameLine.isEmpty()) plugin.getQueenManager().setQueen(plugin.getServer().getOfflinePlayer(nameLine));
                else plugin.getLogger().warning("No queen specified on sign - Queen will be set to Zerek.");

                // Parse minutes from sign and set minutes.
                String minuteLine = PlainTextComponentSerializer.plainText().serialize(plugin.getSign().line(2));
                if (!minuteLine.isEmpty()) plugin.getQueenManager().setActiveQueenSeconds(Integer.parseInt(minuteLine.substring(minuteLine.lastIndexOf(" ")+1))*60);
                else plugin.getLogger().warning("No minutes specified on sign - minutes will be set to 0.");

                // Parse score from sign and set score.
                String scoreLine = PlainTextComponentSerializer.plainText().serialize(plugin.getSign().line(3));
                if (!scoreLine.isEmpty()) plugin.getQueenManager().setQueenScore(Double.parseDouble(scoreLine.substring(scoreLine.lastIndexOf(" ")+1)));
                else plugin.getLogger().warning("No score specified on sign - score will be set to 0.");
            }
            //No sign found.
            else plugin.getLogger().warning("No sign found at: " + signLoc.getBlockX() + " " + signLoc.getBlockY() + " " + signLoc.getBlockZ() + ".");

        }
        else plugin.getLogger().warning("The Queen of The Hill chunk is not yet loaded. Requires a player in the area to start the game.");

        // Check for failed stand or sign assignment and schedule to attempt again in 10 seconds.
        if (plugin.getStand() == null || plugin.getSign() == null) {
            this.plugin.getLogger().info("Failed to start QueenOfTheHill. Trying again in 10 seconds");
            plugin.getServer().getScheduler().runTaskLater(plugin, new InitiateTask(plugin),200L);
        }
        // All checks passed, QueenOfTheHill starting.
        else plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new PlayerCheckTask(plugin), 0L, 20L);

    }
}
