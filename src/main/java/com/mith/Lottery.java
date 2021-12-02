package com.mith;

 import com.mith.Commands.LotteryCMD;
 import com.mith.Commands.LotteryCMDTabCompletion;
 import com.mith.Core.LotteryCore;
 import com.mith.Cycle.LotteryCycle;
 import com.mith.Economy.E_Vault;
 import com.mith.Economy.EconomyType;
 import com.mith.Listeners.BasicListeners;
 import com.mith.Messages.Language;
 import net.milkbowl.vault.economy.Economy;
 import org.bukkit.entity.Player;
 import org.bukkit.plugin.RegisteredServiceProvider;
 import org.bukkit.plugin.java.JavaPlugin;
 import be.maximvdw.placeholderapi.PlaceholderAPI;
 import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
 import be.maximvdw.placeholderapi.PlaceholderReplacer;

 import java.text.DecimalFormat;


public class Lottery
   extends JavaPlugin
 {
   private static Lottery instance;
   private static EconomyType economyType;
   private static Economy economy;
   private static Language language;
   private static LotteryCore lotteryCore;
   private static LotteryCycle lotteryCycle;

   public void onEnable() {
     instance = this;

     saveDefaultConfig();
     getConfig().options().copyDefaults(true);
     saveConfig();

     if (getConfig().getDouble("taxFromPot") < 0.0D || getConfig().getDouble("taxFromPot") > 100.0D) {
       getConfig().set("taxFromPot", 0);
       saveConfig();
     }

     if (!loadEconomy()) {
       return;
     }

     LotteryCMDTabCompletion lotteryCMDTabCompletion = new LotteryCMDTabCompletion();
     getCommand("Lottery").setExecutor(new LotteryCMD());
     getCommand("Lottery").setTabCompleter(lotteryCMDTabCompletion);

     getServer().getPluginManager().registerEvents(new BasicListeners(), this);


     lotteryCore = new LotteryCore();

     lotteryCycle = new LotteryCycle();

     if (getConfig().getBoolean("autoStart")) {
       lotteryCycle.startCycle();
     }
     registerPlaceholders();
     loadLanguage();

     getServer().getConsoleSender().sendMessage(Operations.coloredMessages("&7[AusLotto] Plugin enabled!"));
   }

   public void onDisable() {
     if (lotteryCycle != null && lotteryCycle.isRunning()) {
         lotteryCore.makeResult();
     }

     getServer().getConsoleSender().sendMessage(Operations.coloredMessages("&7[AusLotto] Plugin disabled!"));
   }

   private boolean loadEconomy() {
     if (getConfig().getString("type").toUpperCase().equals("VAULT")) {

       if (!setupEconomy()) {
         getServer().getConsoleSender().sendMessage(Operations.coloredMessages("&7[AusLotto] &cCan not find an economy plugin!"));
         getServer().getConsoleSender().sendMessage(Operations.coloredMessages("&7[AusLotto] &cDisabling plugin!"));
         getPluginLoader().disablePlugin(this);
         return false;
       }

       economyType = new E_Vault();
       getServer().getConsoleSender().sendMessage(Operations.coloredMessages("&7[AusLotto] &7Plugin linked with Vault!"));
       return true;
     }

     getServer().getConsoleSender().sendMessage(Operations.coloredMessages("&7[AusLotto] &cProblem with economy plugin! Use Vault."));
     getPluginLoader().disablePlugin(this);
     return false;
   }

     private boolean setupEconomy() {
         RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

         if (rsp == null)
             return false;

         economy = rsp.getProvider();

         return economy != null;
     }

   public static void loadLanguage() { language = new Language(getInstance().getConfig().getString("language")); }

   public static Lottery getInstance() { return instance; }
   public static Language getLanguage() { return language; }
   public static LotteryCore getLotteryCore() { return lotteryCore; }
   public static LotteryCycle getLotteryCycle() { return lotteryCycle; }

   public static Economy getEconomy() { return economy; }
   public static EconomyType getEconomyType() { return economyType; }



     public void registerPlaceholders()
     {
         PlaceholderAPI.registerPlaceholder(this, "lottery.tickets",
                 new PlaceholderReplacer() {
                     @Override
                     public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
                         Player player = event.getPlayer();
                         return String.valueOf(Lottery.getLotteryCore().getAmountOfTicketsFromPlayer(player));
                     }
                 }
         );
         PlaceholderAPI.registerPlaceholder(this, "lottery.draw",
                 new PlaceholderReplacer() {
                     @Override
                     public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
                         return Operations.parseTimeHours();
                     }
                 }
         );
         PlaceholderAPI.registerPlaceholder(this, "lottery.pot",
                 new PlaceholderReplacer() {
                     @Override
                     public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
                         return String.valueOf(format(Lottery.getLotteryCore().getPriceInPot()));
                     }
                 }
         );
     }

     private static String format(double number) {
         String r = new DecimalFormat("##0E0").format(number);
         String[] suffix = new String[]{"","k", "m", "b", "t"};
         r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
         int MAX_LENGTH = 4;
         while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
             r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
         }
         return r;
     }

 }