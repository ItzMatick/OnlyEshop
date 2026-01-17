package me.itzmatick.onlyEshop;

import org.bukkit.plugin.java.JavaPlugin;

public final class OnlyEshop extends JavaPlugin {

    private Storage storage;
    private GuiFunctions guifunctions;
    private Domains domains;
    private Menu menu;
    private FuzzySearch fuzzysearch;
    private HandleBuyTradeSell handlebuytradesell;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("template.yml", false);
        saveResource("data/arp.yml", false);

        this.handlebuytradesell = new HandleBuyTradeSell(this);
        this.storage = new Storage(this);
        this.guifunctions = new GuiFunctions(this, storage, handlebuytradesell);
        this.domains = new Domains(this, guifunctions);
        this.fuzzysearch = new FuzzySearch();
        this.menu = new Menu(this, storage, domains);

        getCommand("eshop").setExecutor(new Executor(this, storage, guifunctions, domains, menu));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
