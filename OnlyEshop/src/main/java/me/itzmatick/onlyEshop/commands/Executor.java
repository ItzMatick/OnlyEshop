package me.itzmatick.onlyEshop.commands;

import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Domains;
import me.itzmatick.onlyEshop.data.Storage;
import me.itzmatick.onlyEshop.gui.GuiFunctions;
import me.itzmatick.onlyEshop.gui.Menu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Executor implements CommandExecutor {
    private final OnlyEshop plugin;
    private final Storage storage;
    private final GuiFunctions guifunctions;
    private final Domains domains;
    private final Menu menu;

    public Executor (OnlyEshop plugin, Storage storage, GuiFunctions guifunctions, Domains domains, Menu menu) {
        this.plugin = plugin;
        this.storage = storage;
        this.guifunctions = guifunctions;
        this.domains = domains;
        this.menu = menu;
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
                if (strings.length != 3 || !strings[1].equals("edit")) {
                    p.sendMessage("§cUsage: /eshop domain edit <newname>");
                    return true;
                }
                domains.ChangeDomain(uuid, strings[2]);
                break;
            case "title":
                if (strings.length != 3 || !strings[1].equals("edit")) {
                    p.sendMessage("§cUsage: /eshop title edit <newtitle>");
                    return true;
                }
                YamlConfiguration config = storage.ReadFile(uuid);
                config.set("menu.title", strings[2]);
                try {
                    storage.SaveFile(uuid, config);
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage("New title havent been set succesfully");
                }
                break;
            case "menu":
                if (strings.length != 1) {
                    p.sendMessage("§cUsage: /eshop menu");
                    return true;
                }
                menu.searchEshops(null, p);
                break;
            case "edit":
                if (strings.length != 1) {
                    p.sendMessage("§cUsage: /eshop edit");
                    return true;
                }
                guifunctions.EditLayout(p, 0);
                break;
            default:
                p.sendMessage(plugin.getConfig().getString("messages.badarg"));
                return true;
        }
    return true;
    }
}
