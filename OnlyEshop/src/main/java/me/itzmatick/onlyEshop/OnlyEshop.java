package me.itzmatick.onlyEshop;

import me.itzmatick.onlyEshop.commands.Executor;
import me.itzmatick.onlyEshop.data.Domains;
import me.itzmatick.onlyEshop.data.Storage;
import me.itzmatick.onlyEshop.gui.GuiFunctions;
import me.itzmatick.onlyEshop.gui.Menu;
import me.itzmatick.onlyEshop.utils.ChestManager;
import me.itzmatick.onlyEshop.utils.FuzzySearch;
import me.itzmatick.onlyEshop.utils.HandleBuyTradeSell;
import org.bukkit.plugin.java.JavaPlugin;

public final class OnlyEshop extends JavaPlugin {

    private Storage storage;
    private GuiFunctions guifunctions;
    private Domains domains;
    private Menu menu;
    private FuzzySearch fuzzysearch;
    private HandleBuyTradeSell handlebuytradesell;
    private ChestManager chestmanager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("template.yml", false);
        saveResource("data/arp.yml", false);


        this.storage = new Storage(this);
        this.chestmanager = new ChestManager(this, storage);
        this.handlebuytradesell = new HandleBuyTradeSell(this, chestmanager, storage);
        this.guifunctions = new GuiFunctions(this, storage, handlebuytradesell);
        this.domains = new Domains(this, guifunctions);
        this.fuzzysearch = new FuzzySearch();
        this.menu = new Menu(this, storage, domains);



        getServer().getPluginManager().registerEvents(chestmanager, this);

        getCommand("eshop").setExecutor(new Executor(this, storage, guifunctions, domains, menu));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
