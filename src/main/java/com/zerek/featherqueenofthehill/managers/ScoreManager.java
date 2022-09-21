package com.zerek.featherqueenofthehill.managers;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import com.zerek.featherqueenofthehill.data.Score;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScoreManager {

    private final FeatherQueenOfTheHill plugin;
    private final String prefixLineMessage;
    private final String suffixLineMessage;
    private final String leaderboardHeaderMessage;
    private final String leaderboardEntryMessage;
    private final Map<OfflinePlayer, Float> scores = new HashMap<>();


    public ScoreManager(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
        this.leaderboardHeaderMessage = this.plugin.getConfig().getString("leaderboard.header");
        this.leaderboardEntryMessage = this.plugin.getConfig().getString("leaderboard.entry");
        this.prefixLineMessage = this.plugin.getConfig().getString("prefix-line");
        this.suffixLineMessage = this.plugin.getConfig().getString("suffix-line");
        Score.findAll().forEach(q -> scores.put(Bukkit.getOfflinePlayer(UUID.fromString(q.getString("mojang_uuid"))), q.getFloat("score")));
    }

    public void addScore(OfflinePlayer offlinePlayer, Float score){
        if (offlinePlayer.hasPlayedBefore()){
            if (this.hasScore(offlinePlayer)) {
                if (this.scores.get(offlinePlayer) < score) this.scores.put(offlinePlayer,score);
            }
            else this.scores.put(offlinePlayer, score);
        }
    }

    private boolean hasScore(OfflinePlayer offlinePlayer) {
        return this.scores.containsKey(offlinePlayer);
    }


    public void displayTopScores(int amount, CommandSender sender){
        List<OfflinePlayer> TopQueens = this.scores.entrySet().stream().sorted(Map.Entry.<OfflinePlayer, Float>comparingByValue().reversed()).limit(amount).map(Map.Entry::getKey).collect(Collectors.toList());

        Component leaderboard = MiniMessage.miniMessage().deserialize(prefixLineMessage);

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + leaderboardHeaderMessage,
                Placeholder.component("queen", plugin.getChatUtility().addSpacing(Component.text("Queen"),100)),
                Placeholder.component("score", plugin.getChatUtility().addSpacing(Component.text("Score"),30,true))));

        for (OfflinePlayer queen : TopQueens) {
            leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + leaderboardEntryMessage,
                    Placeholder.component("queen", plugin.getChatUtility().addSpacing(Component.text(queen.getName()), 100)),
                    Placeholder.component("score", plugin.getChatUtility().addSpacing(Component.text(String.valueOf(String.format("%.2f", this.scores.get(queen)))), 30, true))));
        }

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + suffixLineMessage));

        sender.sendMessage(leaderboard);
    }

    public void updateDatabase() {
        plugin.getLogger().info("writing to database.");
        this.scores.forEach((q, s) -> {
            Score score = new Score().set("mojang_uuid", q.getUniqueId().toString(), "updated_at", System.currentTimeMillis(), "score", s);
            if (Score.exists(q.getUniqueId().toString())) score.saveIt();
            else score.insert();
        });
    }


}
