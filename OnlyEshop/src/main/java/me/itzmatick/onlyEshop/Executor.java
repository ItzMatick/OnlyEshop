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
    private final Domains domains;

    public Executor (OnlyEshop plugin, Storage storage, GuiFunctions guifunctions, Domains domains) {
        this.plugin = plugin;
        this.storage = storage;
        this.guifunctions = guifunctions;
        this.domains = domains;
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
                    storage.MakeFile(uuid, p);
                    p.sendMessage(plugin.getConfig().getString("messages.eshopcreated"));

                    domains.AddToArp(uuid, "Eshop_of_" + uuid);
                } else {
                    p.sendMessage(plugin.getConfig().getString("messages.alreadyexist"));
                }
                break;

            case "open":
                if (strings.length != 2) {
                    p.sendMessage("§cUsage: /eshop open <uuid>");
                    return true;
                } else {
                    domains.Open(strings[1], p);
                }
                break;
            case "domain":
                if (strings.length != 3 || strings[1] != "edit") {
                    p.sendMessage("§cUsage: /eshop domain edit <newname>");
                    return true;
                }
                domains.ChangeDomain(uuid, strings[2]);
            default:
                p.sendMessage(plugin.getConfig().getString("messages.badarg"));
                return true;
        }
    return true;
    }
}
