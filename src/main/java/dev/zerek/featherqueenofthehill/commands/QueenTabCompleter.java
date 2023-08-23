package dev.zerek.featherqueenofthehill.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QueenTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = new ArrayList<>();
        if (sender.hasPermission("feather.queen.reload") || sender instanceof ConsoleCommandSender) options.add(0, "reload");
        if (sender.hasPermission("feather.queen.current") || sender instanceof ConsoleCommandSender) options.add(0, "current");
        if (sender.hasPermission("feather.queen.leaderboard") || sender instanceof ConsoleCommandSender) options.add(0, "leaderboard");
        return options;
    }
}
