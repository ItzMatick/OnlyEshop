package me.itzmatick.onlyEshop;

import org.bukkit.plugin.java.JavaPlugin;

public final class OnlyEshop extends JavaPlugin {

    private Storage storage;

    @Override
    public void onEnable() {
        getCommand("eshop").setExecutor(new Executor(this, storage));
        this.storage = new Storage(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
