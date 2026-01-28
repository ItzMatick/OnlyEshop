package me.itzmatick.onlyEshop.utils;

import me.itzmatick.onlyEshop.OnlyEshop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private static OnlyEshop plugin;

    public static void init(OnlyEshop instance) {
        plugin = instance;
    }

    public static Component getMessageComponent(String key) {
        if (plugin == null) {
            return Component.text("§cError: Lang class not initialized!");
        }
        FileConfiguration config = plugin.getConfig();
        String msg = config.getString("messages." + key);

        if (msg == null) {
            return Component.text("§cMissing config message: " + key);
        }
        String prefix = config.getString("settings.prefix", "");

        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + msg);
    }

    public static Component replace(Component component, String from, String to) {
        return component.replaceText(config -> {
            config.matchLiteral(from);
            config.replacement(to);
        });
    }

    public static String getString(String key) {
        if (plugin == null) {
            return "";
        }
        FileConfiguration config = plugin.getConfig();
        String msg = config.getString("more." + key);

        if (msg == null) {
            return "";
        }
        return msg;
    }

    public static Component getComponent(String key) {
        if (plugin == null) {
            return Component.text("");
        }
        FileConfiguration config = plugin.getConfig();
        String msg = config.getString("more." + key);

        if (msg == null) {
            return Component.text("");
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(msg);
    }

    public static String getPlain(String key, String defaultValue) {
        if (plugin == null) return defaultValue;

        String msg = plugin.getConfig().getString("more." + key, defaultValue);

        return msg.replaceAll("(?i)&[0-9a-fk-or]", "");
    }

    public static List<Component> getComponentList(String key) {
        List<Component> componentlist = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList(key)) {
            componentlist.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
        }
        return componentlist;
    }
}
