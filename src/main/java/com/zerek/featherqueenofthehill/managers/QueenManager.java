package com.zerek.featherqueenofthehill.managers;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class QueenManager {

    private final FeatherQueenOfTheHill plugin;

    private OfflinePlayer queen;
    private Map<Player,Integer> playersMap = new HashMap<>();
    private final String queenOnHillMessage, timerMessage, newQueenMessage, upcomingQueenMessage, alertWarningMessage, signLine1, signLine2, signLine3;
    private final int requiredSeconds;
    private int activeQueenSeconds = 0;

    public QueenManager(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
        queenOnHillMessage = plugin.getConfig().getString("messages.queen-on-hill-message");
        timerMessage = plugin.getConfig().getString("messages.timer-message");
        newQueenMessage = plugin.getConfig().getString("messages.new-queen-message");
        upcomingQueenMessage = plugin.getConfig().getString("messages.upcoming-queen-message");
        alertWarningMessage = plugin.getConfig().getString("messages.alert-warning-message");
        signLine1 = plugin.getConfig().getString("sign-text.line-1");
        signLine2 = plugin.getConfig().getString("sign-text.line-2");
        signLine3 = plugin.getConfig().getString("sign-text.line-3");
        requiredSeconds = plugin.getConfig().getInt("minutes") * 60;

    }

    public void updateGame(Collection<Player> players, ArmorStand stand, Sign sign){
        this.activeQueenSeconds += 1;
        sign.line(3, MiniMessage.miniMessage().deserialize(signLine3,Placeholder.unparsed("minutes", String.valueOf(activeQueenSeconds/60))));
        sign.update();
        //Remove players who have left the square.
        playersMap = playersMap.entrySet().stream()
                .filter(x -> players.contains(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        players.forEach(p -> {
            //Player is already in square
            if (this.playersMap.containsKey(p)){
                this.playersMap.put(p, this.playersMap.get(p) + 1);

                //Player is not Queen, has been in square for less than requiredSeconds, and is at a 10-second interval.
                if ((!p.equals(queen)) && (this.playersMap.get(p) < this.requiredSeconds) && (this.playersMap.get(p) % 10 == 0)){
                    if (players.contains(queen)) p.sendActionBar(MiniMessage.miniMessage().deserialize(queenOnHillMessage));
                    else p.sendActionBar(MiniMessage.miniMessage().deserialize(timerMessage, Placeholder.unparsed("remaining", String.valueOf(requiredSeconds - playersMap.get(p)))));
                }

                //Player has been in the square for exactly 30 seconds and the player is not the Queen.
                if (this.playersMap.get(p) == 30 && !(p.equals(queen)) && queen.isOnline()) {
                    Player onlineQueen = (Player) queen;
                    onlineQueen.sendMessage(MiniMessage.miniMessage().deserialize(upcomingQueenMessage, Placeholder.unparsed("player",p.getName())));
                    p.sendMessage(MiniMessage.miniMessage().deserialize(alertWarningMessage, Placeholder.unparsed("queen",onlineQueen.getName())));
                }

                //Player has been in the square for requiredSeconds or more, is not the Queen, and the queen is not in the square.
                else if ((!p.equals(queen)) && this.playersMap.get(p) >= this.requiredSeconds && !(players.contains(queen))) crownQueen(p,stand,sign);
            }

            //Player has just entered the square within the last second.
            else {
                plugin.getLogger().info(p.getName() + " entered the Queen of the hill square.");
                this.playersMap.put(p , 1);

            }
        });
    }

    private void crownQueen(Player player, ArmorStand stand, Sign sign){
        stand.getChunk().load();
        plugin.getLogger().info(player.getName() + " is now the Queen of the hill.");
        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(newQueenMessage, Placeholder.unparsed("player",player.getName())));

        this.activeQueenSeconds = 0;

        setQueen(player);

        sign.line(1, MiniMessage.miniMessage().deserialize(signLine1, Placeholder.unparsed("player",player.getName())));
        sign.line(2, MiniMessage.miniMessage().deserialize(signLine2));
        sign.line(3, MiniMessage.miniMessage().deserialize(signLine3,Placeholder.unparsed("minutes", "0")));

        sign.update();

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skull.setItemMeta(skullMeta);
        stand.getEquipment().setHelmet(skull);
    }

    public void setQueen(OfflinePlayer player){
        this.queen = player;
    }
}
