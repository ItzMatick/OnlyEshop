package onlyEshop;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ShowEshop {
    private final OnlyEshop plugin;

    public ShowEshop (OnlyEshop plugin) {
        this.plugin = plugin;
    }

    private Boolean ExistEshop (UUID uuid) {
        File folder = plugin.getDataFolder();
        File file = new File(folder + "players", "ahoj.yml");

        if (!folder.exists()) folder.mkdirs();

        if (file.exists()) {
            return true;
        }
        else {
            return false;
        }
    }
    private void MakeFile (UUID uuid) {
        File folder = plugin.getDataFolder();
        File file = new File(folder + "players", uuid + ".yml");
        File copy = new File(folder + "template.yml");

        try {
            file.createNewFile();
            Files.copy(
                    copy.toPath(),
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String CreateEshop (UUID uuid) {

        if (ExistEshop(uuid)) {
            return "You already have eshop!";
        } else {
            MakeFile(uuid);
            return "Your eshop was created";
        }
    }

    public void OpenEshop (UUID uuid, Player p) {

    }
}
