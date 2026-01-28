package me.itzmatick.onlyEshop.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Domains;
import me.itzmatick.onlyEshop.data.Storage;
import me.itzmatick.onlyEshop.utils.Config;
import me.itzmatick.onlyEshop.utils.HandleBuyTradeSell;
import me.itzmatick.onlyEshop.utils.Materialy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GuiFunctions {
    private final OnlyEshop plugin;
    private final Storage storage;
    private final HandleBuyTradeSell handlebuytradesell;
    private Domains domains;

    public GuiFunctions(OnlyEshop plugin, Storage storage, HandleBuyTradeSell handlebuytradesell) {
        this.plugin = plugin;
        this.storage = storage;
        this.handlebuytradesell = handlebuytradesell;
    }

    public void setDomains(Domains domains) {
        this.domains = domains;
    }

    public void OpenMenu(Player player, UUID uuid, int page) {
        YamlConfiguration config = storage.ReadFile(uuid);

        if (config == null) {
            player.sendMessage(Config.getMessageComponent("unknown-error"));
            return;
        }
        String menu = "menu" + page;
        if (!config.contains(menu)) {
            if (page > 0) {
                OpenMenu(player, uuid, page - 1);
            } else if (page < 0) {
                OpenMenu(player, uuid, page + 1);
            }
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
                            if (action.equalsIgnoreCase("PREVIOUSPAGE")) {
                                OpenMenu(player, uuid, page - 1);
                            }
                            if (action.equalsIgnoreCase("NONE")) {

                            }
                        });

                gui.setItem(slot, guiItem);
                used_slots.remove(Integer.valueOf(slot));
            }
            /*
            Material material = Material.STONE;

            GuiItem guiItem = ItemBuilder.from(material)
                    .name(color("&7Empty slot"))
                    .lore(color("&7Click to edit"))
                    .asGuiItem(event -> {
                        //edit function
                    });

            for (int i : used_slots) {
                gui.setItem(i, guiItem);
            } */
            gui.open(player);

        }
    }

    public static Component color(String text) {
        if (text == null) return Component.empty();
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public void EditLayout (Player player, int page) {
        UUID uuid = player.getUniqueId();

        YamlConfiguration config = storage.ReadFile(uuid);

        if (config == null) {
            player.sendMessage(Config.getMessageComponent("cant-load-edit"));
            return;
        }
        String menu = "menu" + page;
        if (!config.contains(menu)) {
            if (page > 0) {
                OpenMenu(player, uuid, page - 1);
            } else if (page < 0) {
                OpenMenu(player, uuid, page + 1);
            }
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
                                if (action.equalsIgnoreCase("PREVIOUSPAGE")) {
                                    EditLayout(player,page - 1);
                                }
                                if (action.equalsIgnoreCase("SEARCH")) {
                                    gui.close(player);
                                }
                                if (action.equalsIgnoreCase("NONE")) {

                                }
                            } else if (event.isRightClick()) {
                                Edit(player, material, finallore, itemtitle, path, page);
                            }

                        });

                gui.setItem(slot, guiItem);
                used_slots.remove(Integer.valueOf(slot));
            }
            Material material = Material.getMaterial(Config.getString("editor-empty-item-material"));

            GuiItem guiItem = ItemBuilder.from(material)
                    .name(Config.getComponent("editor-empty-item"))
                    .lore(Config.getComponent("editor-empty-item-description"))
                    .asGuiItem(event -> {
                        UUID u = UUID.randomUUID();
                        config.set(menu + ".items." + u, null );
                        config.set(menu + ".items." + u + ".slot", event.getSlot());
                        config.set(menu + ".items." + u + ".material", Config.getPlain("editor-empty-item-material", ""));
                        config.set(menu + ".items." + u + ".action", "NONE");
                        config.set(menu + ".items." + u + ".name", Config.getPlain("editor-default-item-name", ""));
                        config.set(menu + ".items." + u + ".lore", new ArrayList<>());
                        String path = (menu + ".items." + u + ".");
                        storage.SaveFile(uuid, config);

                        Edit(player, material, new ArrayList<>(), "Empty slot", path, page);
                    });

            for (int i : used_slots) {
                gui.setItem(i, guiItem);
            }
            gui.open(player);
        }
    }

    public void Edit (Player player, Material material, List<Component> finallore, String itemtitle, String path, int page) {
        UUID uuid = player.getUniqueId();
        YamlConfiguration config = storage.ReadFile(uuid);

        Gui gui = Gui.gui()
                .title(Config.getComponent("edit-slot"))
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
                .name(Config.getComponent("edit-name"))
                .asGuiItem(event -> {
                    new AnvilGUI.Builder()
                            .plugin(plugin)
                            .title(Config.getPlain("edit-name", ""))
                            .text(itemtitle)
                            .onClick((slot, snapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }
                                String output = snapshot.getText();
                                config.set(path + "name", output);
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, output, path, page);

                                return List.of(AnvilGUI.ResponseAction.close());
                            })
                            .open(player);
                });
        gui.setItem(9, guiItem1);

        GuiItem guiItem2 = ItemBuilder.from(Material.GRASS_BLOCK)
                .name(Config.getComponent("edit-material"))
                .asGuiItem(event -> {


                    List<Material> materials = Materialy.getMaterialy();

                    PaginatedGui paginatedgui = Gui.paginated()
                            .title(Config.getComponent("edit.material"))
                            .rows(6)
                            .pageSize(45)
                            .disableAllInteractions()
                            .create();

                    for (Material mat : materials) {
                        var item = ItemBuilder.from(mat)
                                .asGuiItem(e -> {
                                    config.set(path + "material", mat.toString());
                                    storage.SaveFile(uuid, config);
                                    Edit(player, mat, finallore, itemtitle, path, page);
                                });
                        paginatedgui.addItem(item);
                    }

                    paginatedgui.setItem(6, 2, ItemBuilder.from(Material.ARROW)
                            .name(Config.getComponent("previous-page"))
                            .asGuiItem(e -> paginatedgui.previous()));

                    paginatedgui.setItem(6, 5, ItemBuilder.from(Material.BARRIER)
                            .name(Config.getComponent("back"))
                            .asGuiItem(e -> Edit(player,material, finallore, itemtitle, path, page)));

                    paginatedgui.setItem(6, 8, ItemBuilder.from(Material.ARROW)
                            .name(Config.getComponent("next-page"))
                            .asGuiItem(e -> paginatedgui.next()));

                    paginatedgui.open(player);
                });

        gui.setItem(10, guiItem2);

        GuiItem guiItem3 = ItemBuilder.from(Material.BARRIER)
                .name(Config.getComponent("edit-action"))
                .asGuiItem(event -> {

                    Gui actiongui = Gui.gui()
                            .title(Config.getComponent("edit-action"))
                            .rows(6)
                            .disableAllInteractions()
                            .create();

                    GuiItem gui1 = ItemBuilder.from(Material.CHEST)
                            .name(Config.getComponent("actions.sellplayers"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "BUY");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(0, gui1);

                    GuiItem gui2 = ItemBuilder.from(Material.ENDER_CHEST)
                            .name(Config.getComponent("actions.buyfromplayers"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "SELL");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(1, gui2);

                    GuiItem gui3 = ItemBuilder.from(Material.BARRIER)
                            .name(Config.getComponent("actions.close"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "CLOSE");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(2, gui3);

                    GuiItem gui4 = ItemBuilder.from(Material.ARROW)
                            .name(Config.getComponent("actions.nextpage"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "NEXTPAGE");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(3, gui4);

                    GuiItem gui5 = ItemBuilder.from(Material.ARROW)
                            .name(Config.getComponent("actions.previouspage"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "PREVIOUSPAGE");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(4, gui5);

                    GuiItem gui6 = ItemBuilder.from(Material.BEDROCK)
                            .name(Config.getComponent("actions.none"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "NONE");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(5, gui6);

                    actiongui.open(player);

                });
        gui.setItem(11, guiItem3);

        String defaulttxt = String.join(";", config.getStringList(path + "lore"));

        GuiItem guiItem4 = ItemBuilder.from(Material.BIRCH_SIGN)
                .name(Config.getComponent("edit-lore"))
                .asGuiItem(event -> {
                    new AnvilGUI.Builder()
                            .plugin(plugin)
                            .title(Config.getPlain("edit-lore", ""))
                            .text(defaulttxt)
                            .onClick((slot, snapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }
                                String text = snapshot.getText();

                                String[] rawParts = text.split(";");

                                List<String> finalParts = new ArrayList<>();
                                List<Component> finalComponents = new ArrayList<>();

                                for (int i = 0; i < rawParts.length; i++) {
                                    if (i >= 4) {
                                        player.sendMessage(Config.getMessageComponent("max-lines-4"));
                                        return Collections.emptyList();
                                    }
                                    finalParts.add(rawParts[i]);
                                    finalComponents.add(color(rawParts[i]));
                                }
                                config.set(path + "lore", finalParts);
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finalComponents, itemtitle, path, page);

                                return List.of(AnvilGUI.ResponseAction.close());
                            })
                            .open(player);
                });
        gui.setItem(12, guiItem4);

        GuiItem guiItem5 = ItemBuilder.from(Material.ENDER_EYE)
                .name(Config.getComponent("edit-slot"))
                .asGuiItem(event -> {
                    handlebuytradesell.TypeAnvil("Set price", String.valueOf(config.getInt(path + "slot") + 1), player, material, (amount) -> {
                        int rows = config.getInt("menu" + page + ".rows");
                        int maxslot = (rows * 9);
                        if (amount > maxslot || amount < 1) {
                            player.sendMessage(Config.getMessageComponent("slot-not-found"));
                            Edit(player, material, finallore, itemtitle, path, page);
                        } else {
                            config.set(path + "slot", amount - 1);
                            storage.SaveFile(uuid, config);
                            Edit(player, material, finallore, itemtitle, path, page);
                        }

                    });
                });
        gui.setItem(13, guiItem5);

        GuiItem guiItem10 = ItemBuilder.from(Material.BOOK)
                .name(Config.getComponent("help"))
                .asGuiItem(event -> {

                });
        gui.setItem(14, guiItem10);

        GuiItem guiItem7 = ItemBuilder.from(Material.GOLD_BLOCK)
                .name(Config.getComponent("edit-price"))
                .asGuiItem(event -> {
                    if (config.isSet(path + "action-info")) {
                        double price = config.getDouble(path + "action-info");
                        String text = String.valueOf(price);
                        handlebuytradesell.TypeAnvil(Config.getPlain("edit-price", ""), text, player, material, (amount) -> {
                            config.set(path + "action-info", amount);
                            storage.SaveFile(uuid, config);
                            Edit(player, material, finallore, itemtitle, path, page);
                        });
                    } else {
                        handlebuytradesell.TypeAnvil(Config.getPlain("edit-price", ""), "1", player, material, (amount) -> {
                            config.set(path + "action-info", amount);
                            storage.SaveFile(uuid, config);
                            Edit(player, material, finallore, itemtitle, path, page);
                        });
                    }

                });
        if (config.isSet(path + "action") && (config.getString(path + "action").equals("BUY") || config.getString(path + "action").equals("SELL"))) {
            gui.setItem(15, guiItem7);
        }

        GuiItem guiItem8 = ItemBuilder.from(Material.BARRIER)
                .name(Config.getComponent("close"))
                .asGuiItem(event -> {
                    EditLayout(player, page);
                });
        gui.setItem(16, guiItem8);

        GuiItem guiItem9 = ItemBuilder.from(Material.BARRIER)
                .name(Config.getComponent("delete-item"))
                .asGuiItem(event -> {
                    config.set(path.substring(0, path.length() - 1), null);
                    storage.SaveFile(uuid, config);
                    EditLayout(player, page);
                });
        gui.setItem(17, guiItem9);

        gui.open(player);
    }

    public void OpenSettings(Player player) {
        UUID uuid = player.getUniqueId();
        YamlConfiguration config = domains.ReadFile();
        String path = uuid + ".";

        if (!config.isSet(uuid.toString())) {
            player.sendMessage(Config.getMessageComponent("dont-have-eshop"));
            return;
        }

        Gui gui = Gui.gui()
                .title(Config.getComponent("edit-your-eshop"))
                .rows(2)
                .disableAllInteractions()
                .create();

        Material material = Material.getMaterial(config.getString(path + "menu-material", ""));
        String itemtitle = config.getString(path + "menu-title");
        String domain = config.getString(path + "domain");

        String input = config.getString(path + "menu-description");
        String[] lines = input.split(";");
        List<Component> finallore = new ArrayList<>();

        finallore.add(Component.text("DOMAIN: " + domain));
        for (String line : lines) {
            finallore.add(color(line.trim()));
        }

        GuiItem guiItem = ItemBuilder.from(material)
                .name(color(itemtitle))
                .lore(finallore)
                .asGuiItem();

        gui.setItem(4, guiItem);

        GuiItem guiItem1 = ItemBuilder.from(Material.ANVIL)
                .name(Config.getComponent("edit-search-title"))
                .asGuiItem(event -> {
                    new AnvilGUI.Builder()
                            .plugin(plugin)
                            .title(Config.getPlain("edit-search-title", ""))
                            .text(config.getString("menu-title", " "))
                            .onClick((slot, snapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }
                                String output = snapshot.getText();
                                config.set(path + "menu-title", output);
                                SaveArp(config);
                                OpenSettings(player);
                                return List.of(AnvilGUI.ResponseAction.close());
                            })
                            .open(player);
                });
        gui.setItem(9, guiItem1);

        GuiItem guiItem2 = ItemBuilder.from(Material.GRASS_BLOCK)
                .name(Config.getComponent("edit-material"))
                .asGuiItem(event -> {

                    List<Material> materials = Materialy.getMaterialy();

                    PaginatedGui paginatedgui = Gui.paginated()
                            .title(Config.getComponent("edit-material"))
                            .rows(6)
                            .pageSize(45)
                            .disableAllInteractions()
                            .create();

                    for (Material mat : materials) {
                        var item = ItemBuilder.from(mat)
                                .asGuiItem(e -> {
                                    config.set(path + "menu-material", mat.toString());
                                    SaveArp(config);
                                    OpenSettings(player);
                                });
                        paginatedgui.addItem(item);
                    }

                    paginatedgui.setItem(6, 2, ItemBuilder.from(Material.ARROW)
                            .name(Config.getComponent("previous-page"))
                            .asGuiItem(e -> paginatedgui.previous()));

                    paginatedgui.setItem(6, 5, ItemBuilder.from(Material.BARRIER)
                            .name(Config.getComponent("back"))
                            .asGuiItem(e -> OpenSettings(player)));

                    paginatedgui.setItem(6, 8, ItemBuilder.from(Material.ARROW)
                            .name(Config.getComponent("next-page"))
                            .asGuiItem(e -> paginatedgui.next()));

                    paginatedgui.open(player);
                });

        gui.setItem(10, guiItem2);
        /*
        GuiItem guiItem3 = ItemBuilder.from(Material.BARRIER)
                .name(color("Change sound"))
                .asGuiItem(event -> {

                    Gui actiongui = Gui.gui()
                            .title(color("Edit action"))
                            .rows(6)
                            .disableAllInteractions()
                            .create();

                    GuiItem gui1 = ItemBuilder.from(Material.CHEST)
                            .name(color("SELL TO PLAYERS"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "BUY");
                                SaveFile();
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(0, gui1);

                    GuiItem gui2 = ItemBuilder.from(Material.ENDER_CHEST)
                            .name(color("BUY FROM PLAYERS"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "SELL");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(1, gui2);

                    GuiItem gui3 = ItemBuilder.from(Material.BARRIER)
                            .name(color("CLOSE ESHOP"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "CLOSE");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(2, gui3);

                    GuiItem gui4 = ItemBuilder.from(Material.ARROW)
                            .name(color("NEXT PAGE"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "NEXTPAGE");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(3, gui4);

                    GuiItem gui5 = ItemBuilder.from(Material.ARROW)
                            .name(color("PREVIOUS PAGE"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "PREVIOUSPAGE");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(4, gui5);

                    GuiItem gui6 = ItemBuilder.from(Material.BEDROCK)
                            .name(color("NONE"))
                            .lore()
                            .asGuiItem(inventoryClickEvent -> {
                                config.set(path + "action", "NONE");
                                storage.SaveFile(uuid, config);
                                Edit(player, material, finallore, itemtitle, path, page);
                            });

                    actiongui.setItem(5, gui6);

                    actiongui.open(player);

                });
        gui.setItem(11, guiItem3); */

        GuiItem guiItem4 = ItemBuilder.from(Material.BIRCH_SIGN)
                .name(Config.getComponent("edit-lore"))
                .asGuiItem(event -> {
                    new AnvilGUI.Builder()
                            .plugin(plugin)
                            .title(Config.getPlain("edit-lore", ""))
                            .text(config.getString(path + "menu-description", ""))
                            .onClick((slot, snapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }
                                String text = snapshot.getText();

                                String[] rawParts = text.split(";");

                                for (int i = 0; i < rawParts.length; i++) {
                                    if (i >= 3) {
                                        player.sendMessage(Config.getMessageComponent("max-lines-3"));
                                        return Collections.emptyList();
                                    }
                                }
                                config.set(path + "menu-description", text);
                                SaveArp(config);
                                OpenSettings(player);

                                return List.of(AnvilGUI.ResponseAction.close());
                            })
                            .open(player);
                });
        gui.setItem(12, guiItem4);
        /*
        GuiItem guiItem5 = ItemBuilder.from(Material.ENDER_EYE)
                .name(color("Change slot"))
                .asGuiItem(event -> {
                    handlebuytradesell.TypeAnvil("Set price", String.valueOf(config.getInt(path + "slot")), player, material, (amount) -> {
                        int rows = config.getInt("menu" + page + ".rows");
                        int maxslot = (rows * 9);
                        if (amount > maxslot || amount < 1) {
                            player.sendMessage("This number of slot does not exist - give this page more rows or put different number of slot here");
                            Edit(player, material, finallore, itemtitle, path, page);
                        } else {
                            config.set(path + "slot", amount - 1);
                            storage.SaveFile(uuid, config);
                            Edit(player, material, finallore, itemtitle, path, page);
                        }

                    });
                });
        gui.setItem(13, guiItem5);
        */
        GuiItem guiItem7 = ItemBuilder.from(Material.GOLD_BLOCK)
                .name(Config.getComponent("edit-domain"))
                .asGuiItem(event -> {
                    new AnvilGUI.Builder()
                            .plugin(plugin)
                            .title(Config.getPlain("edit-domain", ""))
                            .text(config.getString(path + "domain"))
                            .onClick((slot, snapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }
                                String output = snapshot.getText();
                                domains.ChangeDomain(uuid, output);
                                OpenSettings(player);
                                return List.of(AnvilGUI.ResponseAction.close());
                            })
                            .open(player);
                });
        gui.setItem(13, guiItem7);

        GuiItem guiItem8 = ItemBuilder.from(Material.DIAMOND)
                .name(Config.getComponent("edit-content"))
                .asGuiItem(event -> {
                    EditLayout(player, 0);
                });

        gui.setItem(11, guiItem8);

        GuiItem guiItem9 = ItemBuilder.from(Material.BARRIER)
                .name(Config.getComponent("close"))
                .asGuiItem(event -> {
                    gui.close(player);
                });

        gui.setItem(17, guiItem9);
        /*
        if (config.isSet(path + "action") && (config.getString(path + "action").equals("BUY") || config.getString(path + "action").equals("SELL"))) {
            gui.setItem(15, guiItem7);
        }

        GuiItem guiItem8 = ItemBuilder.from(Material.BARRIER)
                .name(color("Close"))
                .asGuiItem(event -> {
                    EditLayout(player, page);
                });
        gui.setItem(16, guiItem8);

        GuiItem guiItem9 = ItemBuilder.from(Material.BARRIER)
                .name(color("Delete item"))
                .asGuiItem(event -> {
                    config.set(path.substring(0, path.length() - 1), null);
                    storage.SaveFile(uuid, config);
                    EditLayout(player, page);
                });
        gui.setItem(17, guiItem9);
        */
        gui.open(player);
    }


    public void SaveArp(YamlConfiguration config) {
        File file = new File(plugin.getDataFolder() + "/data","arp.yml");

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("There was a problem with saving arp.yml file");
            e.printStackTrace();
        }
    }
}