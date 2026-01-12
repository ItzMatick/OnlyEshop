package me.itzmatick.onlyEshop;

import org.bukkit.configuration.file.YamlConfiguration;

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

    public void MakeFile(UUID uuid) {
        File file = GetPlayerFile(uuid);
        try {
            InputStream inputstream = plugin.getResource("template.yml");

            if (inputstream == null) {
                file.createNewFile();
                return;
            }
            Files.copy(inputstream, file.toPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}