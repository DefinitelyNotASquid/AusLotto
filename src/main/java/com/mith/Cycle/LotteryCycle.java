 package com.mith.Cycle;

 import com.mith.Lottery;
 import com.mith.Messages.Language;
 import com.mith.Operations;
 import org.bukkit.Bukkit;
 import org.bukkit.entity.Player;
 import org.bukkit.scheduler.BukkitRunnable;

 import java.util.ArrayList;
 import java.util.List;


 public class LotteryCycle
 {
   boolean running;
   Integer countDown;

   public LotteryCycle() { setUpDefaultValues(); }



   private void setUpDefaultValues() { this.countDown = Lottery.getInstance().getConfig().getInt("lotteryCycle"); }



   public void setCountDown(Integer paramInteger) { this.countDown = paramInteger; }


   public void startCycle() {
     this.running = true;
     (new BukkitRunnable()
       {

         public void run()
         {

           if (LotteryCycle.this.countDown == 0) {

             LotteryCycle.this.running = false;
             Lottery.getLotteryCore().makeResult();
             LotteryCycle.this.setUpDefaultValues();

             cancel();

             if (Lottery.getInstance().getConfig().getBoolean("autoStart")) {
               LotteryCycle.this.startCycle();
             }

             return;
           }

           if (LotteryCycle.this.countDown == 60 || LotteryCycle.this.countDown == 300 || LotteryCycle.this.countDown % Lottery.getInstance().getConfig().getInt("infoCycle") == 0) {
             Language language = Lottery.getLanguage();

             Bukkit.broadcastMessage(Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.broadcast.drawIn")
                   .replace("{parsedTime}", Operations.parseTime(LotteryCycle.this.countDown))));

             BroadCastWithContext(language);
           }

           Integer integer1 = LotteryCycle.this.countDown = LotteryCycle.this.countDown - 1;
         }
       }).runTaskTimer(Lottery.getInstance(), 0L, 20L);
   }


   public void BroadCastWithContext(Language language) {

      String preformatted = Operations.coloredMessages(language.getMessage("info.prefix") + " " + language.getMessage("info.broadcast.inPot")
               .replace("{mainBalanceChar}", language.getMessage("balanceChar.main"))
               .replace("{price}", Operations.parseMoney(Lottery.getLotteryCore().getPriceInPot()))
               .replace("{maxTickets}", "" + Lottery.getInstance().getConfig().getInt("maxTickets")));

       List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
       for (Player p: playerList) {
           p.sendMessage(preformatted.replace("{playersTickets}", "" + Lottery.getLotteryCore().getAmountOfTicketsFromPlayer(p)));
       }

   }

   public void endRound() { this.countDown = 1; }



   public boolean isRunning() { return this.running; }



   public Integer getCountDown() { return this.countDown; }
 }