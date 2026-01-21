package me.itzmatick.onlyEshop.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.itzmatick.onlyEshop.utils.HandleBuyTradeSell;
import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Storage;
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
    private final HandleBuyTradeSell handlebuytradesell;

    public GuiFunctions(OnlyEshop plugin, Storage storage, HandleBuyTradeSell handlebuytradesell) {
        this.plugin = plugin;
        this.storage = storage;
        this.handlebuytradesell = handlebuytradesell;
    }

    public void OpenMenu(Player player, UUID uuid) {
        YamlConfiguration config = storage.ReadFile(uuid);

        if (config == null) {
            player.sendMessage(plugin.getConfig().getString("messages.cantload"));
            return;
        }
        String nick = config.getString("nick");
        String title = config.getString("menu.title").replace("%nick%", nick);
        int rows = config.getInt("menu.rows");

        Gui gui = Gui.gui()
                .title(Component.text(title))
                .rows(rows)
                .disableAllInteractions()
                .create();

        ConfigurationSection itemsSection = config.getConfigurationSection("menu.items");

        if (itemsSection != null) {

            List<Integer> used_slots = new ArrayList<>();
            for (int i = 0; i < (rows * 9); i++) {
                used_slots.add(i);
            }

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
                            if (action.equalsIgnoreCase("BUY")) {
                                double price = config.getDouble(path + "action-info");
                                // owner is selling something to the customers
                                handlebuytradesell.Buy(player, mat, price, uuid);
                            }
                            if (action.equalsIgnoreCase("SELL")) {
                                double price = config.getDouble(path + "action-info");
                                // players are selling something to the owner of eshop
                                handlebuytradesell.Sell(player, mat, price, uuid);
                            }
                            if (action.equalsIgnoreCase("NEXTPAGE")) {
                                gui.close(player);
                            }
                            if (action.equalsIgnoreCase("PREVIUSPAGE")) {
                                gui.close(player);
                            }
                            if (action.equalsIgnoreCase("SEARCH")) {
                                gui.close(player);
                            }
                            if (action.equalsIgnoreCase("NONE")) {
                                gui.close(player);
                            }
                        });

                gui.setItem(slot, guiItem);
                used_slots.remove(Integer.valueOf(slot));
            }
            Material material = Material.STONE;

            GuiItem guiItem = ItemBuilder.from(material)
                    .name(Component.text("Empty slot"))
                    .lore(Component.text("Click to edit"))
                    .asGuiItem(event -> {
                        //edit function
                    });

            for (int i : used_slots) {
                gui.setItem(i, guiItem);
            }

        }
        gui.open(player);

    }
}