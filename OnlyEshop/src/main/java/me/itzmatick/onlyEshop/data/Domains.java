package me.itzmatick.onlyEshop.data;

import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.gui.GuiFunctions;
import me.itzmatick.onlyEshop.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class Domains {

    private final OnlyEshop plugin;
    private final GuiFunctions guifunctions;

    public Domains(OnlyEshop plugin, GuiFunctions guifunctions) {

        this.plugin = plugin;
        this.guifunctions = guifunctions;
    }

    public File GetFile() {

        return new File (plugin.getDataFolder(), "data/arp.yml");
    }

    public YamlConfiguration ReadFile() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(GetFile());
        return config;
    }

    public void ChangeDomain(UUID uuid, String newname) {
        YamlConfiguration config = ReadFile();
        Player player = Bukkit.getPlayer(uuid);

        if (config.contains(uuid.toString()) && !IsUUID(newname)) {
            if (!newname.matches("^[a-zA-Z0-9_]{3,16}$")) {
                player.sendMessage(Config.getMessageComponent("domain-invalid"));
                return;
            }
            boolean isTaken = false;

            for (String key : config.getKeys(false)) {
                if (newname.equals(config.getString(key + ".domain"))) {
                    isTaken = true;
                    break;
                }
            }
            if (!isTaken) {
                config.set(uuid + ".domain", newname);
                player.sendMessage(Config.replace(Config.getMessageComponent("domain-changed"),"%domain%", newname));
            } else {
                player.sendMessage(Config.getMessageComponent("domain-taken"));
            }
            try {
                config.save(GetFile());
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(Config.getMessageComponent("unknown-error"));
            }
        } else {
            player.sendMessage(Config.getMessageComponent("domain-invalid"));
        }
    }

    public boolean IsUUID(String name) {
        try {
            UUID test = UUID.fromString(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void AddToArp(UUID uuid, String newname) {
        YamlConfiguration config = ReadFile();

        config.set(uuid + ".domain", newname);
        config.set(uuid + ".menu-title", newname);
        config.set(uuid + ".menu-description", Config.getString("default-eshop-description"));
        config.set(uuid + ".menu-material", Config.getString("default-menu-material"));
        config.set(uuid + ".priority", 0);


        try {
            config.save(GetFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Open(String arg, Player p) {
        YamlConfiguration config = ReadFile();

        if (IsUUID(arg)) {
            try {
                UUID uuid2 = UUID.fromString(arg);
                guifunctions.OpenMenu(p, uuid2, 0);
            } catch (IllegalArgumentException e) {
                p.sendMessage(Config.getMessageComponent("not-valid-uuid"));
            }
        } else {
            UUID uuid = null;

            for (String key : config.getKeys(false)) {
                if (arg.equals(config.getString(key + ".domain"))) {
                    uuid = UUID.fromString(key);
                    try {
                        guifunctions.OpenMenu(p, uuid, 0);
                    } catch (IllegalArgumentException e) {
                        p.sendMessage(Config.getMessageComponent("unknown-error"));
                    }
                    break;
                }
            }
            if (uuid == null) {
                p.sendMessage(Config.getMessageComponent("shop-not-found"));
            }

        }
    }
}