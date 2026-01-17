package me.itzmatick.onlyEshop;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.function.Consumer;

import java.util.Collections;
import java.util.List;

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
