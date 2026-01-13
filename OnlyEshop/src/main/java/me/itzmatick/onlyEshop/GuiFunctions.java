package me.itzmatick.onlyEshop;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiFunctions {
    private final OnlyEshop plugin;
    private final Storage storage;

    public GuiFunctions(OnlyEshop plugin, Storage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public void OpenMenu(Player player, UUID uuid) {
        YamlConfiguration config = storage.ReadFile(uuid);

        if (config == null) {
            player.sendMessage(plugin.getConfig().getString("messages.cantload"));
            return;
        }
        String nick = config.getString("nick");
        String rank = config.getString("rank");
        String title = config.getString("menu.title").replace("%nick%", nick);
        int rows = config.getInt("menu.rows");

        Gui gui = Gui.gui()
                .title(Component.text(title))
                .rows(rows)
                .disableAllInteractions()
                .create();

        ConfigurationSection itemsSection = config.getConfigurationSection("menu.items");

        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                String path = "menu.items." + key + ".";

                int slot = config.getInt(path + "slot");
                String mat = config.getString(path + "material");
                String action = config.getString(path + "action");
                Material material = Material.getMaterial(mat.toUpperCase());
                String itemtitle = config.getString(path + "name");

                List<Component> finallore = new ArrayList<>();
                for (String line : config.getStringList(path + "lore")) {
                    finallore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }

                GuiItem guiItem = ItemBuilder.from(material)
                        .name(Component.text(itemtitle))
                        .lore(finallore)
                        .asGuiItem(event -> {
                            if (action.equalsIgnoreCase("CLOSE")) {
                                gui.close(player);
                            }
                            // Tady můžeš přidat další akce, např. "BUY"
                        });

                gui.setItem(slot, guiItem);
            }
        }

    }
}