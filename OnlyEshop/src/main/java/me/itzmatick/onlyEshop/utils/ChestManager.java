package me.itzmatick.onlyEshop.utils;

import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Storage;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class ChestManager implements Listener {

    private Storage storage;
    private OnlyEshop plugin;

    private final NamespacedKey typeKey;
    private final NamespacedKey ownerKey;

    public ChestManager (OnlyEshop plugin, Storage storage) {
        this.storage = storage;
        this.plugin = plugin;

        this.typeKey = new NamespacedKey(plugin, "chest_type");
        this.ownerKey = new NamespacedKey(plugin, "chest_owner");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (!item.hasItemMeta()) {
            return;
        }
        if (item.getItemMeta().getDisplayName().equals("BUY CHEST") || item.getItemMeta().getDisplayName().equals("SELL CHEST")) {
            UUID uuid = e.getPlayer().getUniqueId();
            String itemname = item.getItemMeta().getDisplayName();
            if (e.getBlockPlaced().getState() instanceof Chest) {
                Chest chest = (Chest) e.getBlockPlaced().getState();

                //add to list of chests
                YamlConfiguration config = storage.ReadFile(uuid);
                Location location = e.getBlockPlaced().getLocation();
                PersistentDataContainer chestPdc = chest.getPersistentDataContainer();

                chestPdc.set(typeKey, PersistentDataType.STRING, itemname);
                chestPdc.set(ownerKey, PersistentDataType.STRING, uuid.toString());

                for (int i = 0; i < 1000; i++) {
                    if (!config.contains("chests.chest" + i)) {
                        config.set("chests.chest" + i, location);
                        chest.setCustomName(itemname + " " + i);
                        chest.update();
                        e.getPlayer().sendMessage("§aYou have succesfully placed eshop chest!");
                        return;
                    }
                }
                e.getPlayer().sendMessage("§aThe maximum amount of chests you can have is 1 thousand. Remove some.");
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if
    }

    public void addLocationToConfig (String type, Location loc, UUID uuid) {
        YamlConfiguration config = storage.ReadFile(uuid);

        List<String> locations = (List<String>) config.getList("chests." + type);
        String locStr = serializeLoc(loc);

        locations.add(locStr);
        config.set("chests." + type, locations);
        storage.SaveFile(uuid, config);
    }

    private String serializeLoc(Location loc) {
        return loc.getWorld().getName() + ";" +
                loc.getBlockX() + ";" +
                loc.getBlockY() + ";" +
                loc.getBlockZ();
    }
}
