package me.itzmatick.onlyEshop.utils;

import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Storage;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        if (item.getItemMeta().getDisplayName().equals("§e§lBUY CHEST") || item.getItemMeta().getDisplayName().equals("§9§lSELL CHEST")) {
            UUID uuid = e.getPlayer().getUniqueId();
            String itemname = item.getItemMeta().getDisplayName();
            if (e.getBlockPlaced().getState() instanceof Chest) {
                Chest chest = (Chest) e.getBlockPlaced().getState();

                YamlConfiguration config = storage.ReadFile(uuid);
                Location location = e.getBlockPlaced().getLocation();
                PersistentDataContainer chestPdc = chest.getPersistentDataContainer();

                chestPdc.set(typeKey, PersistentDataType.STRING, itemname);
                chestPdc.set(ownerKey, PersistentDataType.STRING, uuid.toString());

                addLocationToConfig(itemname, location, uuid);
                chest.setCustomName(itemname);
                chest.update();
                if (item.getItemMeta().getDisplayName().equals("§e§lBUY CHEST")) {
                    e.getPlayer().sendMessage(Config.getMessageComponent("chest-placed-buy"));
                } else if (item.getItemMeta().getDisplayName().equals("§9§lSELL CHEST")) {
                    e.getPlayer().sendMessage(Config.getMessageComponent("chest-placed-sell"));
                }

            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.CHEST) {
            return;
        }
        if (e.getBlock().getState() instanceof TileState) {
            TileState state = (TileState) e.getBlock().getState();
            PersistentDataContainer container = state.getPersistentDataContainer();

            if (container.has(typeKey, PersistentDataType.STRING) || container.has(ownerKey, PersistentDataType.STRING)) {
                String type = container.get(typeKey, PersistentDataType.STRING);
                String owneruuid = container.get(ownerKey, PersistentDataType.STRING);
                UUID uuid = UUID.fromString(owneruuid);
                Location loc = e.getBlock().getLocation();

                Chest chest = (Chest) state;
                for (ItemStack content : chest.getInventory().getContents()) {
                    if (content != null && content.getType() != Material.AIR) {
                        loc.getWorld().dropItemNaturally(loc, content);
                    }
                }
                chest.getInventory().clear();

                removeLocationFromConfig(type, loc, uuid);
                e.setDropItems(false);

                e.getBlock().getWorld().dropItemNaturally(loc, createNamedItem(type));
            }
        }
    }

    public void addLocationToConfig (String type, Location loc, UUID uuid) {
        YamlConfiguration config = storage.ReadFile(uuid);

        List<String> locations = config.getStringList("chests." + type);
        String locStr = serializeLoc(loc);

        locations.add(locStr);
        config.set("chests." + type, locations);
        storage.SaveFile(uuid, config);
    }

    public void removeLocationFromConfig (String type, Location loc, UUID uuid) {
        YamlConfiguration config = storage.ReadFile(uuid);

        List<String> locations = config.getStringList("chests." + type);
        String locStr = serializeLoc(loc);

        locations.remove(locStr);
        config.set("chests." + type, locations);
        storage.SaveFile(uuid, config);
    }

    private String serializeLoc(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }

    public Location deSerializeLoc(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        String [] parts = str.split(";");
        World world = Bukkit.getWorld(parts[0]);
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);

        return new Location(world, x, y, z);
    }

    public ItemStack createNamedItem(String name) {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        e.blockList().removeIf(block -> {
            if (block.getType() != Material.CHEST) return false;
            Chest chest = (Chest) block.getState();
            return chest.getPersistentDataContainer().has(typeKey, PersistentDataType.STRING);
        });
    }
}