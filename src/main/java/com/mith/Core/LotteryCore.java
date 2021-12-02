 package com.mith.Core;

 import com.mith.Lottery;
 import com.mith.Messages.Language;
 import com.mith.Operations;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Random;
 import org.bukkit.Bukkit;
 import org.bukkit.OfflinePlayer;
 import org.bukkit.entity.Player;

 public class LotteryCore {
   HashMap<Player, Integer> playersAndTheirTickets;

   public LotteryCore() {
     this.playersAndTheirTickets = new HashMap();

     this.allTickets = new ArrayList();

     this.extraPot = Double.valueOf(0.0D);


     setExtraPot();
   }
   List<Player> allTickets; Double extraPot;
   public void addPlayerToLottery(Player paramPlayer, Integer paramInteger) {
     Language language = Lottery.getLanguage();

     if (getAmountOfTicketsFromPlayer(paramPlayer).intValue() >= Lottery.getInstance().getConfig().getInt("maxTickets")) {
       paramPlayer.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.maxTicketsReached")));

       return;
     }
     if (getAmountOfTicketsFromPlayer(paramPlayer) + paramInteger > Lottery.getInstance().getConfig().getInt("maxTickets")) {
       paramPlayer.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.overMaxTickets")));

       return;
     }
     double d = getPriceOfTickets(paramInteger);
     if (Lottery.getEconomyType().get(paramPlayer) < d) {
       paramPlayer.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.notEnoughMoney")));

       return;
     }
     if (Lottery.getInstance().getConfig().getBoolean("broadcast.buy")) {
       for (Player player : Bukkit.getOnlinePlayers()) {
         if (paramPlayer == player) {
           continue;
         }
         player.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.broadcast.buy")
               .replace("{player}", paramPlayer.getName())
               .replace("{amountOfTickets}", "" + paramInteger)
               .replace("{ticketWord}", language.getMessage("declension.tickets." + Operations.declension(paramInteger)))
               .replace("{mainBalanceChar}", language.getMessage("balanceChar.main"))
               .replace("{price}", Operations.parseMoney(d))));
       }
     }
     this.playersAndTheirTickets.put(paramPlayer, getAmountOfTicketsFromPlayer(paramPlayer) + paramInteger);
     Lottery.getEconomyType().take(paramPlayer, d);

     if (!Lottery.getInstance().getConfig().getBoolean("autoStart") &&
       !Lottery.getLotteryCycle().isRunning()) {
       Lottery.getLotteryCycle().startCycle();
     }


     paramPlayer.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("success.buy")
           .replace("{player}", paramPlayer.getName())
           .replace("{amountOfTickets}", "" + paramInteger)
           .replace("{ticketWord}", language.getMessage("declension.tickets." + Operations.declension(paramInteger)))
           .replace("{mainBalanceChar}", language.getMessage("balanceChar.main"))
           .replace("{price}", Operations.parseMoney(d))));
   }

   public Integer getAmountOfTicketsFromPlayer(Player paramPlayer) {
     if (!this.playersAndTheirTickets.containsKey(paramPlayer)) {
       return 0;
     }
     return this.playersAndTheirTickets.get(paramPlayer);
   }

   public Integer getAmountOfAllTickets() {
     int i = 0;
     for (Player player : this.playersAndTheirTickets.keySet()) {
       i += this.playersAndTheirTickets.get(player);
     }

     return i;
   }


   public Double getPriceOfTickets(Integer paramInteger) { return paramInteger * Lottery.getInstance().getConfig().getDouble("priceOfTicket"); }


   public Double getPriceInPot() {
     double d = this.extraPot;
     for (Player player : this.playersAndTheirTickets.keySet()) {
       d = d + getPriceOfTickets(this.playersAndTheirTickets.get(player));
     }
     return d;
   }

   public void makeResult() {
     Language language = Lottery.getLanguage();
     if (this.playersAndTheirTickets.size() == 0) {
       for (Player player1 : Bukkit.getOnlinePlayers()) {
         player1.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.nobodyParticipated")));
       }

       return;
     }
     if (this.playersAndTheirTickets.size() < Lottery.getInstance().getConfig().getInt("minPlayers")) {
       for (Player player1 : Bukkit.getOnlinePlayers()) {
         player1.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.lackOfPlayers")));
       }
       refundMoney();
       resetAfterDraw();
       setExtraPot();

       return;
     }
     createListOfAllTickets();

     if (this.allTickets.size() == 0) {
       for (Player player1 : Bukkit.getOnlinePlayers()) {
         player1.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("errors.nobodyParticipated")));
       }

       return;
     }

     Player player = this.allTickets.get((new Random()).nextInt(this.allTickets.size()));

     Integer integer = getAmountOfTicketsFromPlayer(player);

     Double double1 = getPriceInPot();

     for (Player player1 : Bukkit.getOnlinePlayers()) {
       player1.sendMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.broadcast.win")
             .replace("{player}", player.getName())
             .replace("{amountOfTickets}", "" + integer)
             .replace("{ticketWord}", language.getMessage("declension.tickets." + Operations.declension(integer)))
             .replace("{mainBalanceChar}", language.getMessage("balanceChar.main"))
             .replace("{price}", Operations.parseMoney(double1))));
     }



     Double double2 = Operations.calculateTax(double1);

     if (player.isOnline()) {
       Lottery.getEconomyType().give(player, double1 - double2);
     } else {
       OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
       Lottery.getEconomy().depositPlayer(offlinePlayer, double1 - double2);
     }

     resetAfterDraw();
     setExtraPot();
   }


   private void createListOfAllTickets() {
     for (Player player : this.playersAndTheirTickets.keySet()) {
       for (byte b = 1; b <= this.playersAndTheirTickets.get(player); b++) {
         this.allTickets.add(player);
       }
     }

     Collections.shuffle(this.allTickets);
   }

   public void resetAfterDraw() {
     this.allTickets.clear();
     this.playersAndTheirTickets.clear();
   }

   public Double getExtraPot() { return this.extraPot; }



   public void setExtraPot() { this.extraPot = Lottery.getInstance().getConfig().getDouble("extraPot"); }



   public void addToExtraPot(Double paramDouble) { this.extraPot = this.extraPot + paramDouble; }



   public HashMap<Player, Integer> getPlayersAndTheirTicketsMap() { return this.playersAndTheirTickets; }


   public void refundMoney() {
     for (Player player : this.playersAndTheirTickets.keySet()) {
       if (player.isOnline()) {
         Lottery.getEconomyType().give(player, getPriceOfTickets(getAmountOfTicketsFromPlayer(player)));

         continue;
       }
     }
   }

   public boolean isNewPlayerInList(Player paramPlayer) {
     for (Player player : this.playersAndTheirTickets.keySet()) {
       if (player.getName().equals(paramPlayer.getName())) {
         return true;
       }
     }

     return true;
   }

   public Integer getAmountOfTicketsFromNewPlayer(Player paramPlayer) {
     for (Player player : this.playersAndTheirTickets.keySet()) {
       if (player.getName().equals(paramPlayer.getName())) {
         return (Integer)this.playersAndTheirTickets.get(player);
       }
     }
     return Integer.valueOf(0);
   }

   public void removePlayerBecauseOfNewPlayer(Player paramPlayer) {
     for (Player player : this.playersAndTheirTickets.keySet()) {
       if (player.getName().equals(paramPlayer.getName())) {
         this.playersAndTheirTickets.remove(player);
         return;
       }
     }
   }


   public void addNewPlayerToList(Player paramPlayer, Integer paramInteger) { this.playersAndTheirTickets.put(paramPlayer, paramInteger); }
 }


/* Location:              D:\Lottery 2.1.1.jar!\gmail\Sobky\Lottery\Core\LotteryCore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.5
 */