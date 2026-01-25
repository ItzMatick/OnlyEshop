package me.itzmatick.onlyEshop.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Domains;
import me.itzmatick.onlyEshop.data.ShopEntry;
import me.itzmatick.onlyEshop.data.Storage;
import me.itzmatick.onlyEshop.utils.Config;
import me.itzmatick.onlyEshop.utils.FuzzySearch;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Menu {
    private final OnlyEshop plugin;
    private final Storage storage;
    private final Domains domains;

    public Menu(OnlyEshop plugin, Storage storage, Domains domains) {
        this.plugin = plugin;
        this.storage = storage;
        this.domains = domains;
    }

    public List<ShopEntry> getSortedEshops() {
        List<ShopEntry> eshops = new ArrayList<>();
        YamlConfiguration config = domains.ReadFile();

        for (String key : config.getKeys(false)) {
            int priotity = config.getInt(key + ".priority");
            String material = config.getString(key + ".menu-material");
            String title = config.getString(key + ".menu-title");
            String description = config.getString(key + ".menu-description");
            String uuid = key;
            String domain = config.getString(key + ".domain");

            eshops.add(new ShopEntry(uuid, priotity, title, description, material, domain));
        }

        Collections.sort(eshops, Comparator.comparingInt(ShopEntry::getPriority).reversed());

        return eshops;
    }

    public void openMenu(Player p, List<ShopEntry> allshops, boolean x) {

        String title = Config.getString("edit-domain");

        if (allshops.isEmpty()) {
            Gui gui = Gui.gui()
                    .title(Config.getComponent("no-eshops-found-title"))
                    .rows(6)
                    .disableAllInteractions()
                    .create();

            gui.setItem(6, 2, ItemBuilder.from(Material.ARROW)
                    .name(Config.getComponent("previous-page"))
                    .asGuiItem(event -> {}));

            gui.setItem(6, 8, ItemBuilder.from(Material.ARROW)
                    .name(Config.getComponent("next-page"))
                    .asGuiItem(event -> {}));

            gui.setItem(6, 5, ItemBuilder.from(Material.ANVIL)
                    .name(Config.getComponent("search"))
                    .glow(x)
                    .asGuiItem(event -> {
                        if (x == true) {
                            searchEshops(null, p);
                        } else {
                            openSearchAnvil(p);
                        }
                    }));
            gui.open(p);
            return;
        }

        PaginatedGui gui = Gui.paginated()
                .title(Component.text(title))
                .rows(6)
                .pageSize(45)
                .disableAllInteractions()
                .create();

        for (ShopEntry eshop : allshops) {
            String materialname = eshop.getMaterial().toUpperCase();
            Material material = Material.getMaterial(materialname);

            if (material == null) {
                material = Material.STONE;
            }
            String input = eshop.getDescription();
            String[] lines = input.split(";");
            List<Component> finallore = new ArrayList<>();

            finallore.add(Component.text("DOMAIN: " + eshop.getDomain()));
            for (String line : lines) {
                finallore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line.trim()));
            }

            var item = ItemBuilder.from(material)
                    .name(Component.text(eshop.getTitle()))
                    .lore(finallore)
                    .asGuiItem(event -> {
                        p.sendMessage(Config.replace(Config.getMessageComponent("opening-eshop"), "%domain%", eshop.getDomain()));
                        p.closeInventory();

                        domains.Open(eshop.getDomain(), p);
                    });
            gui.addItem(item);
        }

        gui.setItem(6, 2, ItemBuilder.from(Material.ARROW)
                .name(Config.getComponent("previous-page"))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(6, 8, ItemBuilder.from(Material.ARROW)
                .name(Config.getComponent("next-page"))
                .asGuiItem(event -> gui.next()));

        gui.setItem(6, 5, ItemBuilder.from(Material.ANVIL)
                .name(Config.getComponent("search"))
                .glow(x)
                .asGuiItem(event -> {
                    if (x == true) {
                        searchEshops(null, p);
                    } else {
                        openSearchAnvil(p);
                    }
                }));


        gui.open(p);
    }

    public void searchEshops(String searched, Player p) {
        List<ShopEntry> allshops = getSortedEshops();
        List<ShopEntry> finalshops = new ArrayList<>();

        if (searched == null || searched.isEmpty()) {
            openMenu(p, allshops, false);
            return;
        }

        for (ShopEntry eshop : allshops) {
            int newpriority = eshop.getPriority();
            String domain = eshop.getDomain();
            String description = eshop.getDescription();
            String title = eshop.getTitle();

            double domainsimilarity = FuzzySearch.getSimilarity(domain, searched);
            double titlesimlilarity = FuzzySearch.getSimilarity(title, searched);
            double descriptionsimlilarity = FuzzySearch.getSimilarity(description, searched);

            int finalpriority = (int) Math.round((domainsimilarity * 50) + (titlesimlilarity * 35) + (descriptionsimlilarity * 20) + eshop.getPriority());

            if (domainsimilarity == 1) {
                finalpriority = finalpriority + 50;
            }
            if (titlesimlilarity == 1) {
                finalpriority = finalpriority + 30;
            }

            if (finalpriority > 20) {
                ShopEntry xy = new ShopEntry(
                        eshop.getUuid(),
                        finalpriority,
                        eshop.getTitle(),
                        eshop.getDescription(),
                        eshop.getMaterial(),
                        eshop.getDomain()
                );

                finalshops.add(xy);
            }
        }
        Collections.sort(finalshops, Comparator.comparingInt(ShopEntry::getPriority).reversed());
        openMenu(p, finalshops, true);
    }

    public void openSearchAnvil(Player p) {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        new AnvilGUI.Builder()
                .plugin(plugin)
                .title(Config.getPlain("search", ""))
                .itemLeft(new ItemStack(Material.PAPER))
                .text(Config.getPlain("default-search-text", ""))
                .onClick((slot, snapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String text = snapshot.getText();
                    if (text.equalsIgnoreCase(Config.getPlain("default-search-text", ""))) {
                        p.sendMessage(Config.getMessageComponent("write-something"));
                        return Collections.emptyList();
                    }
                    searchEshopsDelayed(text, p);
                    atomicBoolean.set(true);
                    return List.of(AnvilGUI.ResponseAction.close());
                })
                .onClose(snapshot -> {
                    if (!atomicBoolean.get()) {
                        searchEshopsDelayed(null, p);
                    }
                })
                .open(p);


    }

    public void searchEshopsDelayed(String text, Player p) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            searchEshops(text, p);

        });
    }
}
