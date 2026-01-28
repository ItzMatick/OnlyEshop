package me.itzmatick.onlyEshop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EshopTab implements TabCompleter {

    private static final List<String> COMMANDS = Arrays.asList(
            "create", "menu", "edit", "help", "reload", "title edit <title>", "domain edit <domain>"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], COMMANDS, new ArrayList<>());
        }
        return new ArrayList<>();
    }
}