package me.itzmatick.onlyEshop.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Storage;
import me.itzmatick.onlyEshop.utils.HandleBuyTradeSell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
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

    public void OpenMenu(Player player, UUID uuid, int page) {
        YamlConfiguration config = storage.ReadFile(uuid);

        if (config == null) {
            player.sendMessage(color(plugin.getConfig().getString("messages.cantload")));
            return;
        }
        String menu = "menu" + page;
        if (!config.contains(menu)) {
            if (page > 0) {
                OpenMenu(player, uuid, page - 1);
            } else if (page < 0) {
                OpenMenu(player, uuid, page + 1);
            }
            player.sendMessage("This page does not exist");
            return;
        }

        int rows = config.getInt(menu + ".rows");
        String nick = config.getString("nick");
        String title = config.getString(menu + ".title").replace("%nick%", nick);

        Gui gui = Gui.gui()
                .title(color(title))
                .rows(rows)
                .disableAllInteractions()
                .create();

        ConfigurationSection itemsSection = config.getConfigurationSection(menu + ".items");

        if (itemsSection != null) {

            List<Integer> used_slots = new ArrayList<>();
            for (int i = 0; i < (rows * 9); i++) {
                used_slots.add(i);
            }

            for (String key : itemsSection.getKeys(false)) {
                String path = menu + ".items." + key + ".";

                int slot = config.getInt(path + "slot");
                String mat = config.getString(path + "material");
                String action = config.getString(path + "action");
                Material material = Material.getMaterial(mat.toUpperCase());
                String itemtitle = config.getString(path + "name");

                List<Component> finallore = new ArrayList<>();
                for (String line : config.getStringList(path + "lore")) {
                    finallore.add(color(line));
                }

                GuiItem guiItem = ItemBuilder.from(material)
                        .name(color(itemtitle))
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
                                OpenMenu(player, uuid, page + 1);
                            }
                            if (action.equalsIgnoreCase("PREVIUSPAGE")) {
                                OpenMenu(player, uuid, page - 1);
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
                    .name(color("&7Empty slot"))
                    .lore(color("&7Click to edit"))
                    .asGuiItem(event -> {
                        //edit function
                    });

            for (int i : used_slots) {
                gui.setItem(i, guiItem);
            }
            gui.open(player);
        }
    }

    private Component color(String text) {
        if (text == null) return Component.empty();
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public void EditLayout (Player player, int page) {
        UUID uuid = player.getUniqueId();

        YamlConfiguration config = storage.ReadFile(uuid);

        if (config == null) {
            player.sendMessage(color(plugin.getConfig().getString("messages.cantload")));
            return;
        }
        String menu = "menu" + page;
        if (!config.contains(menu)) {
            if (page > 0) {
                OpenMenu(player, uuid, page - 1);
            } else if (page < 0) {
                OpenMenu(player, uuid, page + 1);
            }
            player.sendMessage("This page does not exist");
            return;
        }

        int rows = config.getInt(menu + ".rows");
        String nick = config.getString("nick");
        String title = config.getString(menu + ".title").replace("%nick%", nick);

        Gui gui = Gui.gui()
                .title(color(title))
                .rows(rows)
                .disableAllInteractions()
                .create();

        ConfigurationSection itemsSection = config.getConfigurationSection(menu + ".items");

        if (itemsSection != null) {

            List<Integer> used_slots = new ArrayList<>();
            for (int i = 0; i < (rows * 9); i++) {
                used_slots.add(i);
            }

            for (String key : itemsSection.getKeys(false)) {
                String path = menu + ".items." + key + ".";

                int slot = config.getInt(path + "slot");
                String mat = config.getString(path + "material");
                String action = config.getString(path + "action");
                Material material = Material.getMaterial(mat.toUpperCase());
                String itemtitle = config.getString(path + "name");

                List<Component> finallore = new ArrayList<>();
                for (String line : config.getStringList(path + "lore")) {
                    finallore.add(color(line));
                }

                GuiItem guiItem = ItemBuilder.from(material)
                        .name(color(itemtitle))
                        .lore(finallore)
                        .asGuiItem(event -> {
                            if (event.isLeftClick()) {
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
                                    EditLayout(player,page + 1);
                                }
                                if (action.equalsIgnoreCase("PREVIUSPAGE")) {
                                    EditLayout(player,page - 1);
                                }
                                if (action.equalsIgnoreCase("SEARCH")) {
                                    gui.close(player);
                                }
                                if (action.equalsIgnoreCase("NONE")) {
                                    gui.close(player);
                                }
                            } else if (event.isRightClick()) {
                                Edit(player, material, finallore, itemtitle, path);
                            }

                        });

                gui.setItem(slot, guiItem);
                used_slots.remove(Integer.valueOf(slot));
            }
            Material material = Material.STONE;

            GuiItem guiItem = ItemBuilder.from(material)
                    .name(color("&7Empty slot"))
                    .lore(color("&7Click to edit"))
                    .asGuiItem(event -> {
                        //edit function
                    });

            for (int i : used_slots) {
                gui.setItem(i, guiItem);
            }
            gui.open(player);
        }
    }

    public void Edit (Player player, Material material, List<Component> finallore, String itemtitle, String path) {
        UUID uuid = player.getUniqueId();
        YamlConfiguration config = storage.ReadFile(uuid);

        Gui gui = Gui.gui()
                .title(color("Edit slot"))
                .rows(2)
                .disableAllInteractions()
                .create();

        //current item
        GuiItem guiItem = ItemBuilder.from(material)
                .name(color(itemtitle))
                .lore(finallore)
                .asGuiItem();

        gui.setItem(4, guiItem);





        GuiItem guiItem1 = ItemBuilder.from(Material.ANVIL)
                .name(color("Edit name"))
                .asGuiItem(event -> {
                    new AnvilGUI.Builder()
                            .plugin(plugin)
                            .title("Edit item title")
                            .text(itemtitle)
                            .onClick((slot, snapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }
                                String output = snapshot.getText();
                                config.set(path + "name", output);
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, output, path);

                                return List.of(AnvilGUI.ResponseAction.close());
                            })
                            .open(player);
                });
        gui.setItem(9, guiItem1);

        GuiItem guiItem2 = ItemBuilder.from(Material.GRASS_BLOCK)
                .name(color("Change material"))
                .asGuiItem(event -> {

                });
        gui.setItem(10, guiItem2);

        GuiItem guiItem3 = ItemBuilder.from(Material.BARRIER)
                .name(color("Change action"))
                .asGuiItem(event -> {

                });
        gui.setItem(11, guiItem3);

        GuiItem guiItem4 = ItemBuilder.from(Material.BIRCH_SIGN)
                .name(color("Change lore"))
                .asGuiItem(event -> {

                });
        gui.setItem(12, guiItem4);

        GuiItem guiItem5 = ItemBuilder.from(Material.ENDER_EYE)
                .name(color("Change slot"))
                .asGuiItem(event -> {

                });
        gui.setItem(13, guiItem5);

        GuiItem guiItem6 = ItemBuilder.from(Material.CHEST)
                .name(color("Change model data"))
                .asGuiItem(event -> {

                });
        gui.setItem(14, guiItem6);

        gui.open(player);
    }
}