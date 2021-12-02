package com.mith.Economy;

import org.bukkit.entity.Player;

public interface EconomyType {
  void take(Player paramPlayer, Double paramDouble);
  
  void give(Player paramPlayer, Double paramDouble);
  
  Double get(Player paramPlayer);
}