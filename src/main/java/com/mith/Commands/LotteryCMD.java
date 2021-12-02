 package com.mith.Commands;

 import com.mith.Lottery;
 import com.mith.Messages.Language;
 import com.mith.Operations;
 import org.bukkit.Bukkit;
 import org.bukkit.command.Command;
 import org.bukkit.command.CommandExecutor;
 import org.bukkit.command.CommandSender;
 import org.bukkit.entity.Player;

 import java.util.ArrayList;

 import static com.mith.Operations.playerHelp;
 import static com.mith.Operations.playerInfo;


 public class LotteryCMD
   implements CommandExecutor
 {
   public boolean onCommand(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString) {
     if (paramCommand.getName().equalsIgnoreCase("Lottery")) {
       if (paramCommandSender instanceof org.bukkit.command.ConsoleCommandSender) {
         Language language = Lottery.getLanguage();
         if (paramArrayOfString.length == 0) {
           paramCommandSender.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.consoleError")));
           return true;
         }

         if (paramArrayOfString[0].equalsIgnoreCase("addToPot")) {
           if (paramArrayOfString.length != 2) {
             paramCommandSender.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongUsageOfCommand")));
             return true;
           }

           if (!paramCommandSender.hasPermission("Lottery.admin")) {
             paramCommandSender.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.noPermissions")));
             return true;
           }

           if (!Operations.isDouble(paramArrayOfString[1])) {
             paramCommandSender.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.notNumber")));
             return true;
           }

           double d = Operations.parseDouble(paramArrayOfString[1]);

           if (d <= 0.0D) {
             paramCommandSender.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongAmountExtraPot")));
             return true;
           }

           Lottery.getLotteryCore().addToExtraPot(d);
           paramCommandSender.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("success.addToExtraPot")
                 .replace("{price}", Operations.parseMoney(d))
                 .replace("{mainBalanceChar}", language.getMessage("balanceChar.main"))));
           return true;
         }


         paramCommandSender.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.consoleError")));
         return true;
       }

       if (paramCommandSender instanceof Player) {
         Player player = (Player)paramCommandSender;

         Language language = Lottery.getLanguage();
         if(paramArrayOfString.length == 0)
         {
           playerHelp(player);
           return true;
         }

         if (paramArrayOfString.length > 2) {
           player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongUsageOfCommand")));
           return true;
         }

         if (paramArrayOfString[0].equalsIgnoreCase("buy")) {

           if (!player.hasPermission("Lottery.player")) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.noPermissions")));
             return true;
           }

           if (paramArrayOfString.length == 1) {
             Lottery.getLotteryCore().addPlayerToLottery(player, 1);
             return true;
           }

           if (!Operations.isInt(paramArrayOfString[1])) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.notNumber")));
             return true;
           }

           Integer integer = Operations.parseInt(paramArrayOfString[1]);

           if (integer < 1 || integer > Lottery.getInstance().getConfig().getInt("maxTickets")) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongAmount")));
             return true;
           }

           Lottery.getLotteryCore().addPlayerToLottery(player, integer);
           return true;
         }

         if (paramArrayOfString[0].equalsIgnoreCase("reload")) {
           if (paramArrayOfString.length != 1) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongUsageOfCommand")));
             return true;
           }

           if (!player.hasPermission("Lottery.admin")) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.noPermissions")));
             return true;
           }

           Lottery.getInstance().reloadConfig();
           if (Lottery.getInstance().getConfig().getInt("minPlayers") < 1) {
             Lottery.getInstance().getConfig().set("minPlayers", 1);
           }
           Lottery.getInstance().saveConfig();

           Lottery.loadLanguage();

           player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("success.reload")));
           return true;
         }

         if (paramArrayOfString[0].equalsIgnoreCase("draw")) {
           if (paramArrayOfString.length != 1) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongUsageOfCommand")));
             return true;
           }

           if (!player.hasPermission("Lottery.admin")) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.noPermissions")));
             return true;
           }

           if (!Lottery.getLotteryCycle().isRunning()) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.notRunning")));
             return true;
           }

           if (Lottery.getLotteryCycle().getCountDown() < 5) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.drawEndsInAFewSeconds")));
             return true;
           }

           Lottery.getLotteryCycle().endRound();

           Bukkit.broadcastMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.broadcast.manualDraw")));

           return true;
         }

         if (paramArrayOfString[0].equalsIgnoreCase("addToPot")) {
           if (paramArrayOfString.length != 2) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongUsageOfCommand")));
             return true;
           }

           if (!player.hasPermission("Lottery.admin")) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.noPermissions")));
             return true;
           }

           if (!Operations.isDouble(paramArrayOfString[1])) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.notNumber")));
             return true;
           }

           double d = Operations.parseDouble(paramArrayOfString[1]);

           if (d <= 0.0D) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongAmountExtraPot")));
             return true;
           }

           if (Lottery.getEconomyType().get(player) < d) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.notEnoughMoneyToAddThemIntoPot")));
             return true;
           }

           Lottery.getLotteryCore().addToExtraPot(d);

           Lottery.getEconomyType().take(player, d);

           player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("success.addToExtraPot")
                 .replace("{price}", Operations.parseMoney(d))
                 .replace("{mainBalanceChar}", language.getMessage("balanceChar.main"))));
           return true;
         }

         if (paramArrayOfString[0].equalsIgnoreCase("info")) {
           if (paramArrayOfString.length != 1) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongUsageOfCommand")));
             return true;
           }

           if (!player.hasPermission("Lottery.player")) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.noPermissions")));
             return true;
           }

           playerInfo(player);
           return true;
         }

         if (paramArrayOfString[0].equalsIgnoreCase("help")) {
           if (paramArrayOfString.length != 1) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.wrongUsageOfCommand")));
             return true;
           }

           if (!player.hasPermission("Lottery.player")) {
             player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.noPermissions")));
             return true;
           }

           playerHelp(player);
           return true;
         }
       }
     }
     return false;
   }
 }