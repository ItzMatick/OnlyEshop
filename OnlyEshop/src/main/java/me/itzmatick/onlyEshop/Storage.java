package me.itzmatick.onlyEshop;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public class Storage {

    private final OnlyEshop plugin;

    public Storage (OnlyEshop plugin) {
        this.plugin = plugin;

        new File (plugin.getDataFolder(), "data/player").mkdirs();
    }

    private File GetPlayerFile(UUID uuid) {
        return new File (plugin.getDataFolder(), "data/player/" + uuid.toString() + ".yml");
    }

    public void MakeFile(UUID uuid, Player player) {
        File file = GetPlayerFile(uuid);
        try {
            InputStream inputstream = plugin.getResource("template.yml");

            if (inputstream == null) {
                return;
            }
            Files.copy(inputstream, file.toPath());

        } catch (IOException e) {
        }
        player.sendMessage("Your eshop has been created!");
        YamlConfiguration config = ReadFile(uuid);
        config.set("nick", player.getName());
        config.set("uuid", uuid.toString());

        SaveFile(uuid, config);
    }

    public YamlConfiguration ReadFile(UUID uuid) {

        YamlConfiguration config = YamlConfiguration.loadConfiguration(GetPlayerFile(uuid));

        return config;
    }

    public boolean ExistFile(UUID uuid) {

        if (GetPlayerFile(uuid).exists()) {
            return true;
        } else {
            return false;
        }
    }

    public void SaveFile(UUID uuid, YamlConfiguration config) {
        try {
            config.save(GetPlayerFile(uuid));
        } catch (IOException e) {
            plugin.getLogger().severe("Plugin was not able to save data of player with UUID: " + uuid);
            e.printStackTrace();
        }
    }
}