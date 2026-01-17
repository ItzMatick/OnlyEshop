package me.itzmatick.onlyEshop.data;

import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.gui.GuiFunctions;
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

        if (config.contains(uuid.toString()) && !IsUUID(newname)) {
            boolean isTaken = false;

            for (String key : config.getKeys(false)) {
                if (newname.equals(config.getString(key + ".domain"))) {
                    isTaken = true;
                    break;
                }
            }
            if (!isTaken) {
                config.set(uuid + ".domain", newname);
            }
            try {
                config.save(GetFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        config.set(uuid + ".menu-description", "nothing was set");
        config.set(uuid + ".menu-material", "STONE");
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
                guifunctions.OpenMenu(p, uuid2);
            } catch (IllegalArgumentException e) {
                p.sendMessage("§cNot a valid UUID!");
            }
        } else {
            UUID uuid = null;

            for (String key : config.getKeys(false)) {
                if (arg.equals(config.getString(key + ".domain"))) {
                    uuid = UUID.fromString(key);
                    try {
                        guifunctions.OpenMenu(p, uuid);
                    } catch (IllegalArgumentException e) {
                        p.sendMessage("§cSomething failed!");
                    }
                    break;
                }
            }
            if (uuid == null) {
                p.sendMessage("There isnt any shop with this domain");
            }

        }
    }
}