 package com.mith.Listeners;

 import com.mith.Lottery;
 import com.mith.Operations;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.player.PlayerJoinEvent;


 public class BasicListeners
   implements Listener
 {
   @EventHandler
   public void joinPlayer(PlayerJoinEvent paramPlayerJoinEvent) {
     Player player = paramPlayerJoinEvent.getPlayer();

     if (Lottery.getLotteryCore().isNewPlayerInList(player)) {
       Integer integer = Lottery.getLotteryCore().getAmountOfTicketsFromNewPlayer(player);
       Lottery.getLotteryCore().removePlayerBecauseOfNewPlayer(player);
       Lottery.getLotteryCore().addNewPlayerToList(player, integer);
     }

     if (Lottery.getInstance().getConfig().getBoolean("sendJoinInfo") && Lottery.getLotteryCycle().isRunning()) {
         Operations.playerInfo(player);
     }
   }
 }