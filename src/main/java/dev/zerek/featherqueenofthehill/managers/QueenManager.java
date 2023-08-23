package dev.zerek.featherqueenofthehill.managers;

import dev.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
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
        private int activeQueenSeconds = 0;
    private Float queenScore = (float) 0;

    private Map<Player,Integer> playersMap = new HashMap<>();
    private final String currentQueenInfoMessage, currentQueenWaitMessage, queenOnHillMessage, timerMessage, newQueenMessage, upcomingQueenMessage, alertWarningMessage, signLine1, signLine2, signLine3, signLine4;
    private final int requiredSeconds;


    public QueenManager(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
        this.queen = this.plugin.getServer().getOfflinePlayer("Zerek");
        this.currentQueenInfoMessage = plugin.getConfig().getString("messages.current-queen-info");
        this.currentQueenWaitMessage = plugin.getConfig().getString("messages.current-queen-wait");
        this.queenOnHillMessage = plugin.getConfig().getString("messages.queen-on-hill");
        this.timerMessage = plugin.getConfig().getString("messages.timer");
        this.newQueenMessage = plugin.getConfig().getString("messages.new-queen");
        this.upcomingQueenMessage = plugin.getConfig().getString("messages.upcoming-queen");
        this.alertWarningMessage = plugin.getConfig().getString("messages.alert-warning");
        this.signLine1 = plugin.getConfig().getString("sign-text.line-1");
        this.signLine2 = plugin.getConfig().getString("sign-text.line-2");
        this.signLine3 = plugin.getConfig().getString("sign-text.line-3");
        this.signLine4 = plugin.getConfig().getString("sign-text.line-4");
        this.requiredSeconds = plugin.getConfig().getInt("minutes") * 60;

    }

    public void updateGame(Collection<Player> players, ArmorStand stand, Sign sign){

        // Update Queen statistics.
        this.activeQueenSeconds += 1;
        this.addScore((float) 0.0001);

        // Update sign Statistics lines.
        sign.line(2, MiniMessage.miniMessage().deserialize(signLine3,Placeholder.unparsed("minutes", String.valueOf(activeQueenSeconds/60))));
        sign.line(3, MiniMessage.miniMessage().deserialize(signLine4,Placeholder.unparsed("score", String.valueOf(String.format("%.2f", queenScore)))));
        sign.update();

        // Remove players who have left the square.
        playersMap = playersMap.entrySet().stream().filter(x -> players.contains(x.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Consider each player in the square.
        players.forEach(p -> {


            // Player has just entered the square within the runnable interval (1 second).
            if (!this.playersMap.containsKey(p)) this.playersMap.put(p , 1);

            //Player is already in the square and is not queen
            else {

                this.playersMap.put(p, this.playersMap.get(p) + 1);

                if (this.isQueenSet() && !this.isQueen(p)) {

                    // Check if player has been in the square at a 10-second interval and not long enough to be crowned.
                    if ((this.playersMap.get(p) < this.requiredSeconds) && (this.playersMap.get(p) % 10 == 0)){

                        // Tell the player the queen is on the hill.
                        if (isQueenOnHill(players)) p.sendActionBar(MiniMessage.miniMessage().deserialize(queenOnHillMessage));

                            //tell the player the remaining time till they are crowned.
                        else p.sendActionBar(MiniMessage.miniMessage().deserialize(timerMessage, Placeholder.unparsed("remaining", String.valueOf(requiredSeconds - playersMap.get(p)))));
                    }

                    // Check if the player has been in the square for exactly 30 seconds and the Queen is online. Warn the player and alert the Queen.
                    if (this.playersMap.get(p) == 30 && this.isQueenOnline()) {
                        queen.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(upcomingQueenMessage, Placeholder.unparsed("player",p.getName())));
                        p.sendMessage(MiniMessage.miniMessage().deserialize(alertWarningMessage, Placeholder.unparsed("queen",queen.getPlayer().getName())));
                    }

                    // Check if the player has been in the square for the required seconds to be crowned and either the queen is offline or outside the square. Crown the new Queen.
                    else if ((this.playersMap.get(p) >= this.requiredSeconds) && !this.isQueenOnHill(players)) crownQueen(p, stand, sign);
                }
            }
        });
    }

    private void crownQueen(Player player, ArmorStand stand, Sign sign){
        stand.getChunk().load();
        plugin.getScoreManager().addScore(this.queen,this.queenScore);
        plugin.getLogger().info(player.getName() + " is now the Queen of the hill.");
        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(newQueenMessage, Placeholder.unparsed("player",player.getName())));

        this.activeQueenSeconds = 0;
        this.queenScore = (float) 0;

        setQueen(player);

        sign.line(0, MiniMessage.miniMessage().deserialize(signLine1, Placeholder.unparsed("player",player.getName())));
        sign.line(1, MiniMessage.miniMessage().deserialize(signLine2));
        sign.line(2, MiniMessage.miniMessage().deserialize(signLine3,Placeholder.unparsed("minutes", String.valueOf(activeQueenSeconds/60))));
        sign.line(3, MiniMessage.miniMessage().deserialize(signLine4,Placeholder.unparsed("score", String.valueOf(String.format("%.2f", queenScore)))));

        sign.update();

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skull.setItemMeta(skullMeta);
        stand.getEquipment().setHelmet(skull);
    }

    public void setQueen(OfflinePlayer offlinePlayer){
        this.queen = offlinePlayer;
        plugin.getLogger().info(offlinePlayer.getName() + " has been set as the Queen.");
    }

    public boolean isQueen(Player player){
        return queen.getUniqueId().equals(player.getUniqueId());
    }

    public void setActiveQueenSeconds(int activeQueenSeconds) {
        this.activeQueenSeconds = activeQueenSeconds;
    }

    public void setQueenScore(Float queenScore) {
        this.queenScore = queenScore;
    }

    private boolean isQueenSet(){
        return this.queen != null;
    }

    public boolean isQueenOnline(){
        if (this.isQueenSet()) return this.queen.isOnline();
        return false;
    }

    public boolean isQueenOnHill(Collection<Player> players){
        return this.isQueenOnline() && players.contains(queen.getPlayer());
    }

    public OfflinePlayer getQueen(){
        return this.queen;
    }

    public void addScore(Float addedScore){
        this.queenScore += addedScore;
    }

    public void displayCurrentQueenInfo(CommandSender sender) {
        if (isQueenSet() && queen.getName() != null){
            sender.sendMessage(MiniMessage.miniMessage().deserialize(currentQueenInfoMessage,
                    Placeholder.unparsed("queen",queen.getName()),
                    Placeholder.unparsed("minutes", String.valueOf(activeQueenSeconds/60)),
                    Placeholder.unparsed("score", String.valueOf(String.format("%.2f", queenScore)))));
        }
        else sender.sendMessage(MiniMessage.miniMessage().deserialize(currentQueenWaitMessage));
    }
}
