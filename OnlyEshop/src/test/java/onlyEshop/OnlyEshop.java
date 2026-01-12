package onlyEshop;

import org.bukkit.plugin.java.JavaPlugin;

public final class OnlyEshop extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("eshop").setExecutor(new Executor(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
