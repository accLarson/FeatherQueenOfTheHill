package com.zerek.featherqueenofthehill.commands;

import com.zerek.featherqueenofthehill.FeatherQueenOfTheHill;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class QueenCommand implements CommandExecutor {
    private final FeatherQueenOfTheHill plugin;

    public QueenCommand(FeatherQueenOfTheHill plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;
        if (args.length == 1){
            switch (args[0].toLowerCase()){
                case "reload":
                    if (sender.hasPermission("feather.queen.reload") || sender instanceof ConsoleCommandSender) plugin.reload(sender);
                    return true;
                case "current":
                    if (sender.hasPermission("feather.queen.current") || sender instanceof ConsoleCommandSender) plugin.getQueenManager().displayCurrentQueenInfo(sender);
                    return true;
                case "leaderboard":
                    if (sender.hasPermission("feather.queen.leaderboard") || sender instanceof ConsoleCommandSender) plugin.getScoreManager().displayTopScores(10,sender);
                    return true;
                default:
                    sender.sendMessage(ChatColor.of("#E4453A") + "Invalid Command");
                    return true;
            }
        }
        else sender.sendMessage(ChatColor.of("#E4453A") + "Invalid Command");
        return true;
    }
}
