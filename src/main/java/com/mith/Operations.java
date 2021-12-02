 package com.mith;

 import com.mith.Messages.Language;
 import java.text.DecimalFormat;
 import java.text.DecimalFormatSymbols;
 import java.util.ArrayList;
 import java.util.List;
 import org.bukkit.ChatColor;
 import org.bukkit.entity.Player;

 public class Operations
 {
   public static String coloredMessages(String paramString) { return ChatColor.translateAlternateColorCodes('&', paramString); }



   public static String stripColor(String paramString) { return ChatColor.stripColor(paramString); }


   public static boolean isInt(String paramString) {
     try {
       Integer.parseInt(paramString);
       return true;
     } catch (NumberFormatException numberFormatException) {
       return false;
     }
   }

   public static Integer parseInt(String paramString) {
     try {
       return Integer.parseInt(paramString);
     } catch (NumberFormatException numberFormatException) {
       return 0;
     }
   }

   public static boolean isDouble(String paramString) {
     try {
       Double.parseDouble(paramString);
       return true;
     } catch (NumberFormatException numberFormatException) {
       return false;
     }
   }

   public static double parseDouble(String paramString) {
     try {
       return Double.parseDouble(paramString);
     } catch (NumberFormatException numberFormatException) {
       return 0.0D;
     }
   }

   public static String parseMoney(Double paramDouble) {
     DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

     if (!Lottery.getInstance().getConfig().getBoolean("useDecimalFormat")) {
       decimalFormat = new DecimalFormat("#,###");
     }

     DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
     decimalFormatSymbols.setDecimalSeparator('.');
     decimalFormatSymbols.setGroupingSeparator(',');
     decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
     return decimalFormat.format(paramDouble);
   }

   public static String parseTime(Integer paramInteger) {
     int i = paramInteger / 3600;
     int j = (paramInteger - i * 3600) / 60;
     int k = paramInteger - i * 3600 - 60 * j;
     return "" + i + " " + Lottery.getLanguage().getMessage("declension.hours." + declension(i)) + ", " + j + " " + Lottery.getLanguage().getMessage("declension.minutes." + declension(Integer.valueOf(j))) + " " + Lottery.getLanguage().getMessage("info.andWord") + " " + k + " " + Lottery.getLanguage().getMessage("declension.seconds." + declension(Integer.valueOf(k)));
   }

   public static String parseTimeHours() {
     return String.valueOf((Lottery.getLotteryCycle().getCountDown()/60));
   }

   public static String parseTimeToDigitalFormat(Integer paramInteger) {
     int i = paramInteger / 3600;
     int j = (paramInteger - i * 3600) / 60;
     int k = paramInteger - i * 3600 - 60 * j;
     return String.format("%02d", new Object[] {i}) + ":" + String.format("%02d", new Object[] {j}) + ":" + String.format("%02d", new Object[] {k});
   }

   public static String declension(Integer paramInteger) {
     if (paramInteger.intValue() == 1)
       return "one";
     if (paramInteger.intValue() > 1 && paramInteger.intValue() < 5) {
       return "moreThanOne";
     }
     return "moreThanFour";
   }

   public static Double calculateTax(Double paramDouble) {
     double d = Lottery.getInstance().getConfig().getDouble("taxFromPot") / 100.0D;

     if (d == 0.0D) {
       return Double.valueOf(0.0D);
     }

     return Double.valueOf(paramDouble.doubleValue() * d);
   }

   public static void playerHelp(Player player)
   {
     Language language = Lottery.getLanguage();

     List<String> subCommands =  new ArrayList<>();
     if(player.hasPermission("Lottery.admin"))
     {
       subCommands.add(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.help.reload") + "\n"));
       subCommands.add(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.help.draw") + "\n"));
       subCommands.add(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.help.addToPot") + "\n"));
     }

     if(player.hasPermission("Lottery.player"))
     {
       subCommands.add(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.help.buy") + "\n"));
       subCommands.add(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.help.info") + "\n"));
       subCommands.add(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.help.help") + "\n"));
     }

     String s = "";
     for (String k: subCommands) {
       s = s + k;
     }

     player.sendMessage(s);
   }



   public static void playerInfo(Player player){

     Language language = Lottery.getLanguage();

     player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.broadcast.drawIn")
             .replace("{parsedTime}", Operations.parseTime(Lottery.getLotteryCycle().getCountDown()))));

     player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.broadcast.inPot")
             .replace("{mainBalanceChar}", language.getMessage("balanceChar.main"))
             .replace("{price}", Operations.parseMoney(Lottery.getLotteryCore().getPriceInPot()))
             .replace("{maxTickets}", "" + Lottery.getInstance().getConfig().getInt("maxTickets"))
             .replace("{playersTickets}", "" + Lottery.getLotteryCore().getAmountOfTicketsFromPlayer(player))
            ));

   }
 }