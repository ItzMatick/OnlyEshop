package me.itzmatick.onlyEshop;

import org.bukkit.plugin.java.JavaPlugin;

public final class OnlyEshop extends JavaPlugin {

    private Storage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("template.yml", false);

        this.storage = new Storage(this);
        getCommand("eshop").setExecutor(new Executor(this, storage));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
