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

        TypeAnvil("Amount", "1", p, material, (amount) -> {
            double balance = VaultHook.getBalance(p);
            int invspace = canFit(p, item);
            OfflinePlayer owner = Bukkit.getOfflinePlayer(owneruuid);
            int amountint = amount.intValue();

            if (amount * price <= balance) {
                if (invspace >= amount) {
                    if (itemsOwnerHas(material, owneruuid, "§e§lBUY CHEST") >= amount) {
                        String error = VaultHook.withdraw(p, amount * price);
                        if (error == null || error.isEmpty()) {
                            VaultHook.deposit(owner, amount * price);
                            manageChestItems(amount, item, owneruuid, "§e§lBUY CHEST");
                            item.setAmount(amountint);
                            p.getInventory().addItem(item);
                        } else {
                            p.sendMessage("Při platbě došlo k chybě");
                        }
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
        ItemStack item = new ItemStack(material);

        TypeAnvil("Amount", "1",p, material, (amount) -> {
            double balance = VaultHook.getBalance(offlinePlayer);
            ItemStack itemToGive = new ItemStack(material);
            int amountint = amount.intValue();

            if (amount * price <= balance) {
                if (p.getInventory().containsAtLeast(itemToGive, amountint)) {
                    if (itemSpaceInChests(owneruuid, "§9§lSELL CHEST", material) >= amount) {
                        VaultHook.withdraw(offlinePlayer, amount * price);
                        VaultHook.deposit(p, amount * price);
                        manageChestItems(amount, item, owneruuid, "§9§lSELL CHEST");
                        itemToGive.setAmount(amountint);
                        p.getInventory().removeItem(itemToGive);
                    } else {
                        p.sendMessage("Owner does not have storage big enough for this");
                    }
                } else {
                    p.sendMessage("You dont have enough items in your inventory");
                }
            } else {
                p.sendMessage("Owner of this shop doesnt have money for this");
            }
        });
    }


    public void TypeAnvil(String title, String defaulttext, Player p, Material material, Consumer<Double> callback) {
        new AnvilGUI.Builder()
                .plugin(plugin)
                .title(title)
                .itemLeft(new ItemStack(material))
                .text(defaulttext)
                .onClick((slot, snapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String text = snapshot.getText();
                    String substring = text;
                    char lastchar = text.charAt(text.length() - 1);
                    int multiplier = 1;
                    if (Character.isLetter(lastchar)) {
                        if (text.length() < 2) {
                            p.sendMessage("You need to also type number, not only suffix");
                            return Collections.emptyList();
                        }
                        switch (lastchar) {
                            case 'k', 'K':
                                multiplier = 1000;
                                break;
                            case 'b', 'B':
                                multiplier = 1000000000;
                                break;
                            case 'm', 'M':
                                multiplier = 1000000;
                                break;
                            default:
                                p.sendMessage("You cant use this symbol here");
                                return Collections.emptyList();
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
            if (loc == null) continue;

            Chunk chunk = loc.getChunk();
            boolean isLoaded = loc.isChunkLoaded();

            if (!isLoaded) {
                chunk.load();
            }

            if (loc.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) loc.getBlock().getState();

                for (ItemStack slot : chest.getInventory().getContents()) {
                    if (slot == null || slot.getType() == Material.AIR) {
                        result = result + item.getMaxStackSize();
                    } else if (slot.isSimilar(item)) {
                        result = result + item.getMaxStackSize() - slot.getAmount();
                    }
                }
            }
        }
        return result;
    }

    public int itemsOwnerHas(Material mat, UUID uuid, String type) {
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
            if (loc.getBlock().getState() instanceof Chest) {
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
        }
        return result;
    }

    public void manageChestItems(double amountDouble, ItemStack itemOriginal, UUID owneruuid, String type) {
        int amount = (int) amountDouble;
        YamlConfiguration config = storage.ReadFile(owneruuid);
        List<String> stringloc = config.getStringList("chests." + type);

        ItemStack stackToProcess = itemOriginal.clone();
        stackToProcess.setAmount(amount);

        for (String locStr : stringloc) {
            if (amount <= 0) break;

            Location location = chestmanager.deSerializeLoc(locStr);
            if (location == null) continue;

            boolean wasLoaded = location.isChunkLoaded();
            Chunk chunk = location.getChunk();
            if (!wasLoaded) chunk.load();

            if (location.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) location.getBlock().getState();

                if (type.equals("§e§lBUY CHEST")) {
                    stackToProcess.setAmount(amount);

                    var leftovers = chest.getInventory().removeItem(stackToProcess);

                    if (leftovers.isEmpty()) {
                        amount = 0;
                    } else {
                        ItemStack remainingItem = leftovers.values().iterator().next();
                        amount = remainingItem.getAmount();
                    }
                } else if (type.equals("§9§lSELL CHEST")) {

                    stackToProcess.setAmount(amount);

                    var leftovers = chest.getInventory().addItem(stackToProcess);

                    if (leftovers.isEmpty()) {
                        amount = 0;
                    } else {
                        ItemStack remainingItem = leftovers.values().iterator().next();
                        amount = remainingItem.getAmount();
                    }
                }
            }
        }
    }
}