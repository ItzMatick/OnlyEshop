package me.itzmatick.onlyEshop.utils;

import me.itzmatick.onlyEshop.OnlyEshop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class HandleBuyTradeSell {

    private OnlyEshop plugin;

    public HandleBuyTradeSell(OnlyEshop plugin) {
        this.plugin = plugin;
    }

    public void Buy(Player p, String matName, double price) {
        Material material = Material.getMaterial(matName.toUpperCase());

        TypeAnvil(p, material, (amount) -> {
            double balance = VaultHook.getBalance(p);

            if (amount * price <= balance) {
                ItemStack itemToGive = new ItemStack(material);
                while (amount >= 1) {
                    p.give(itemToGive);
                    amount--;
                }
                VaultHook.withdraw(p, amount * price);
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









}
