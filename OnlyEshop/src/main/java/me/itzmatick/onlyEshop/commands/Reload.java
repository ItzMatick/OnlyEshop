package me.itzmatick.onlyEshop.commands;

import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.utils.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {

    private final OnlyEshop plugin;

    public Reload (OnlyEshop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command");
            return true;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("only.eshop.reload")) {
            player.sendMessage(Config.getMessageComponent("no-permission"));
            return true;
        } else {
            player.sendMessage(Config.getMessageComponent("config-reloaded"));
            plugin.reloadConfig();
            return true;
        }
    }
}
