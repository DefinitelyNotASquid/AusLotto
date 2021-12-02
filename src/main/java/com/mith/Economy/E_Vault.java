 package com.mith.Economy;

 import com.mith.Lottery;
 import org.bukkit.entity.Player;

 public class E_Vault
   implements EconomyType
 {
   public void take(Player paramPlayer, Double paramDouble) { Lottery.getEconomy().withdrawPlayer(paramPlayer, paramDouble); }
   public void give(Player paramPlayer, Double paramDouble) { Lottery.getEconomy().depositPlayer(paramPlayer, paramDouble); }
   public Double get(Player paramPlayer) { return Lottery.getEconomy().getBalance(paramPlayer); }
 }