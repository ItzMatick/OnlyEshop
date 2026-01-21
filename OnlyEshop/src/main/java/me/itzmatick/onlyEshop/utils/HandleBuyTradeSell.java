package me.itzmatick.onlyEshop.utils;

import me.itzmatick.onlyEshop.OnlyEshop;
import me.itzmatick.onlyEshop.data.Storage;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class HandleBuyTradeSell {

    private OnlyEshop plugin;
    private final ChestManager chestmanager;
    private final Storage storage;

    public HandleBuyTradeSell(OnlyEshop plugin, ChestManager chestmanager, Storage storage) {
        this.plugin = plugin;
        this.chestmanager = chestmanager;
        this.storage = storage;
    }

    public void Buy(Player p, String matName, double price, UUID owneruuid) {
        Material material = Material.getMaterial(matName.toUpperCase());
        ItemStack item = new ItemStack(material, 1);

        TypeAnvil(p, material, (amount) -> {
            double balance = VaultHook.getBalance(p);
            int invspace = canFit(p, item);
            OfflinePlayer owner = Bukkit.getOfflinePlayer(owneruuid);

            if (amount * price <= balance) {
                if (invspace >= amount) {
                    if (itemsOwnerHas(material, owneruuid, "BUY CHEST") >= amount) {
                        while (amount >= 1) {
                            p.getInventory().addItem(item);
                            amount--;
                        }

                        VaultHook.withdraw(p, amount * price);
                        VaultHook.deposit(owner, amount * price);
                    } else {
                        p.sendMessage("Owner does not have this much items.");
                    }
                } else {
                    p.sendMessage("You dont have enough space in your inventory");
                }
            } else {
                p.sendMessage("You dont have enough money to buy this");
            }
        });
    }

    public void Sell(Player p, String matName, double price, UUID owneruuid) {
        Material material = Material.getMaterial(matName.toUpperCase());
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owneruuid);

        TypeAnvil(p, material, (amount) -> {
            double balance = VaultHook.getBalance(offlinePlayer);
            ItemStack itemToGive = new ItemStack(material);

            if (amount * price <= balance) {
                if (p.getInventory().containsAtLeast(itemToGive, (int) Math.round(price))) {
                    while (amount >= 1) {
                        //offlinePlayer.give(itemToGive);
                        amount--;
                    }
                    VaultHook.withdraw(offlinePlayer, amount * price);
                    VaultHook.deposit(p, amount * price);
                } else {
                    p.sendMessage("You dont have enough items in your inventory");
                }

            } else {
                p.sendMessage("Owner of this shop doesnt have money for this");
            }
        });
    }


    public void TypeAnvil(Player p, Material material, Consumer<Double> callback) {
        new AnvilGUI.Builder()
                .plugin(plugin)
                .title("Amount")
                .itemLeft(new ItemStack(material))
                .text("1")
                .onClick((slot, snapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String text = snapshot.getText();
                    String substring = text;
                    char lastchar = text.charAt(text.length() - 1);
                    int multiplier = 1;
                    if (Character.isLetter(lastchar)) {
                        switch (lastchar) {
                            case 'k','K': multiplier = 1000; break;
                            case 'b','B': multiplier = 1000000000; break;
                            case 'm','M': multiplier = 1000000; break;
                            default: p.sendMessage("You cant use this symbol here"); return Collections.emptyList();
                        }
                        substring = text.substring(0, text.length() - 1);
                    }
                    try {
                        double result = Double.parseDouble(substring) * multiplier;
                        callback.accept(result);
                        return List.of(AnvilGUI.ResponseAction.close());
                    } catch (NumberFormatException e) {
                        p.sendMessage("This is not valid format");
                        return Collections.emptyList();
                    }
                })
                .open(p);
    }

    public int canFit(Player p, ItemStack itemadd) {
        int result = 0;
        for (ItemStack slot : p.getInventory().getStorageContents()) {
            if (slot == null || slot.getType() == Material.AIR) {
                result = result + itemadd.getMaxStackSize();
            } else if (slot.isSimilar(itemadd)) {
                int space = slot.getType().getMaxStackSize() - slot.getAmount();
                result = result + space;
            }
        }
        return result;
    }

    public int itemSpaceInChests(UUID uuid, String type, Material mat) {
        YamlConfiguration config = storage.ReadFile(uuid);
        ItemStack item = new ItemStack(mat);

        int result = 0;

        List<String> list = config.getStringList("chests." + type);
        for (String activeChest : list) {
            Location loc = chestmanager.deSerializeLoc(activeChest);

            Chunk chunk = loc.getChunk();
            boolean isLoaded = loc.isChunkLoaded();

            if (!isLoaded) {
                chunk.load();
            }

            if(loc.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) loc.getBlock().getState();

                for (ItemStack slot : chest.getInventory().getContents()) {
                    if (item != null && slot.isSimilar(item)) {
                        result = result + item.getMaxStackSize() - slot.getAmount();
                    } else if (item != null || slot.isEmpty()) {
                        result = result + item.getMaxStackSize();
                    }
                }
            }

            if (!isLoaded) {
                chunk.unload();
            }

        }
        return result;
    }

    public int itemsOwnerHas (Material mat, UUID uuid, String type) {
        YamlConfiguration config = storage.ReadFile(uuid);
        ItemStack item = new ItemStack(mat);

        int result = 0;

        List<String> list = config.getStringList("chests." + type);
        for (String activeChest : list) {
            Location loc = chestmanager.deSerializeLoc(activeChest);
            if (loc == null) continue;

            Chunk chunk = loc.getChunk();
            boolean isLoaded = loc.isChunkLoaded();

            if (!isLoaded) {
                chunk.load();
            }
            if(loc.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) loc.getBlock().getState();

                for (ItemStack slot : chest.getInventory().getContents()) {
                    if (slot == null) {
                        continue;
                    }
                    if (slot.isSimilar(item)) {
                        result = result + slot.getAmount();
                    }
                }
            }
            if (!isLoaded) {
                chunk.unload();
            }
        }
        return result;
    }

    public void removeItems(int amount, ) {
        
    }
}
