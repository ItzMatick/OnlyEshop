package me.itzmatick.onlyEshop;

import org.bukkit.plugin.java.JavaPlugin;

public final class OnlyEshop extends JavaPlugin {

    private Storage storage;
    private GuiFunctions guifunctions;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("template.yml", false);

        this.storage = new Storage(this);
        this.guifunctions = new GuiFunctions(this, storage);

        getCommand("eshop").setExecutor(new Executor(this, storage, guifunctions));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
