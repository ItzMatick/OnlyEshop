package me.itzmatick.onlyEshop.commands;

import me.itzmatick.onlyEshop.utils.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class Help {

    public static void send(Player player) {
        List<Component> help = Config.getComponentList("messages.help");

        for (Component line : help) {
            player.sendMessage(line);
        }
    }
}