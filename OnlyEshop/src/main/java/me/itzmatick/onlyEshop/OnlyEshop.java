package me.itzmatick.onlyEshop;

import org.bukkit.plugin.java.JavaPlugin;

public final class OnlyEshop extends JavaPlugin {

    private Storage storage;
    private GuiFunctions guifunctions;
    private Domains domains;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("template.yml", false);
        saveResource("data/arp.yml", false);

        this.storage = new Storage(this);
        this.guifunctions = new GuiFunctions(this, storage);
        this.domains = new Domains(this, guifunctions);



        getCommand("eshop").setExecutor(new Executor(this, storage, guifunctions, domains));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
