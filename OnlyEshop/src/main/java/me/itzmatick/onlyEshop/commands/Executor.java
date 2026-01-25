package me.itzmatick.onlyEshop.commands;

import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Domains;
import me.itzmatick.onlyEshop.data.Storage;
import me.itzmatick.onlyEshop.gui.GuiFunctions;
import me.itzmatick.onlyEshop.gui.Menu;
import me.itzmatick.onlyEshop.utils.Config;
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
            commandSender.sendMessage(Config.getMessageComponent("only-players"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(Config.getMessageComponent("invalid-args"));
            return true;
        }

        Player p = (Player) commandSender;
        UUID uuid = p.getUniqueId();

        switch (strings[0]) {
            case "create":
                if (!storage.ExistFile(uuid)) {
                    storage.MakeFile(uuid, p);

                    String msg = Config.getString("default-domain").replace("%uuid%", uuid.toString());
                    domains.AddToArp(uuid, msg);
                } else {
                    p.sendMessage(Config.getMessageComponent("shop-already-exists"));
                }
                guifunctions.OpenSettings(p);
                break;

            case "open":
                if (strings.length != 2) {
                    p.sendMessage(Config.getMessageComponent("eshop-open-usage"));
                    return true;
                } else {
                    domains.Open(strings[1], p);
                }
                break;
            case "domain":
                if (strings.length != 3 || !strings[1].equals("edit")) {
                    p.sendMessage(Config.getMessageComponent("eshop-domain-edit-usage"));
                    return true;
                }
                domains.ChangeDomain(uuid, strings[2]);
                break;
            case "title":
                if (strings.length != 3 || !strings[1].equals("edit")) {
                    p.sendMessage(Config.getMessageComponent("eshop-title-edit-usage"));
                    return true;
                }
                YamlConfiguration config = storage.ReadFile(uuid);
                config.set("menu.title", strings[2]);
                try {
                    storage.SaveFile(uuid, config);
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(Config.getMessageComponent("unknown-error"));
                }
                break;
            case "menu":
                if (strings.length != 1) {
                    p.sendMessage(Config.getMessageComponent("eshop-menu-usage"));
                    return true;
                }
                menu.searchEshops(null, p);
                break;
            case "edit":
                if (strings.length != 1) {
                    p.sendMessage(Config.getMessageComponent("eshop-edit-usage"));
                    return true;
                }
                guifunctions.OpenSettings(p);
                break;
            default:
                p.sendMessage(Config.getMessageComponent("invalid-args"));
                return true;
        }
    return true;
    }
}
