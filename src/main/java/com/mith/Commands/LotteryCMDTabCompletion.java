package com.mith.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotteryCMDTabCompletion implements TabCompleter {
    /**
     * The list of /cosmetic sub-commands.
     */

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        // The possible completions
        List<String> completions = new ArrayList<>();
        // Gets the matches
        StringUtil.copyPartialMatches(args[0], getSubCommands(sender), completions);
        // Sort the completions
        Collections.sort(completions);
        return completions;
    }

    public List<String> getSubCommands(CommandSender sender)
    {
        List<String> subCommands =  new ArrayList<>();
        Player player = (Player)sender;
        if(player.hasPermission("Lottery.admin"))
        {
            subCommands.add("addToPot");
            subCommands.add("draw");
            subCommands.add("reload");
        }

        if(player.hasPermission("Lottery.player"))
        {
            subCommands.add("buy");
            subCommands.add("info");
            subCommands.add("help");
        }

        return subCommands;
    }

}

