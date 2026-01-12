package me.itzmatick.onlyEshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Executor implements CommandExecutor {
    private final OnlyEshop plugin;
    private final Storage storage;

    public Executor (OnlyEshop plugin, Storage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (commandSender instanceof Player) {
            if (strings[0].isEmpty()) {
                commandSender.sendMessage(plugin.getConfig().getString("messages.noarg"));
                return true;
            }
        } else {
            commandSender.sendMessage(plugin.getConfig().getString("messages.bad_instance"));
            return true;
        }

        Player p = (Player) commandSender;
        UUID uuid = p.getUniqueId();

        switch (strings[0]) {
            case "create":
                if (!storage.ExistFile(uuid)) {
                    storage.MakeFile(uuid);
                }

            case "menu":


            default:
                p.sendMessage(plugin.getConfig().getString("messages.badarg"));
                return true;
        }
    }
}
