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
    private final GuiFunctions guifunctions;

    public Executor (OnlyEshop plugin, Storage storage, GuiFunctions guifunctions) {
        this.plugin = plugin;
        this.storage = storage;
        this.guifunctions = guifunctions;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(plugin.getConfig().getString("messages.bad_instance"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(plugin.getConfig().getString("messages.noarg"));
            return true;
        }

        Player p = (Player) commandSender;
        UUID uuid = p.getUniqueId();

        switch (strings[0]) {
            case "create":
                if (!storage.ExistFile(uuid)) {
                    storage.MakeFile(uuid);
                    p.sendMessage(plugin.getConfig().getString("messages.eshopcreated"));
                } else {
                    p.sendMessage(plugin.getConfig().getString("messages.alreadyexist"));
                }
                break;
            case "open":
                if (strings[1] != null) {
                    UUID shopuuid = UUID.fromString(strings[1]);
                    guifunctions.OpenMenu(p, shopuuid);
                }

            default:
                p.sendMessage(plugin.getConfig().getString("messages.badarg"));
                return true;
        }
    return true;
    }
}
