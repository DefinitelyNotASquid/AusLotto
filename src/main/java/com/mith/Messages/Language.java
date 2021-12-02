 package com.mith.Messages;

 import com.mith.Lottery;
 import com.mith.Operations;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.nio.charset.Charset;
 import java.util.List;
 import org.bukkit.configuration.InvalidConfigurationException;
 import org.bukkit.configuration.file.FileConfiguration;
 import org.bukkit.configuration.file.YamlConfiguration;




 public class Language
 {
   private FileConfiguration Config;
   String language;

   public Language(String paramString) {
     this.language = paramString;
     loadNewFile();
     loadConfig();
   }

   public void loadConfig() {
     File file = new File(Lottery.getInstance().getDataFolder(), this.language.toLowerCase() + ".yml");
     try {
       FileInputStream fileInputStream = new FileInputStream(file);
       this.Config = new YamlConfiguration();
       this.Config.load(new InputStreamReader(fileInputStream, Charset.forName("UTF-8")));
     } catch (FileNotFoundException fileNotFoundException) {
       fileNotFoundException.printStackTrace();
     } catch (IOException iOException) {
       iOException.printStackTrace();
     } catch (InvalidConfigurationException invalidConfigurationException) {
       invalidConfigurationException.printStackTrace();
     }
   }


   public void loadNewFile() {
     File file = new File(Lottery.getInstance().getDataFolder(), this.language.toLowerCase() + ".yml");
     if (!file.exists()) {
       Lottery.getInstance().saveResource(this.language.toLowerCase() + ".yml", false);
       try {
         Thread.sleep(100L);
       } catch (InterruptedException interruptedException) {
         interruptedException.printStackTrace();
       }
       Lottery.getInstance().getServer().getConsoleSender().sendMessage(Operations.coloredMessages("&7[AusLotto] &aFile for language {language} created!".replace("{language}", this.language)));
     }
   }

   public String getLanguage() { return this.language; }



   public String getMessage(String paramString) { return this.Config.getString(paramString); }



   public Integer getInt(String paramString) { return Integer.valueOf(this.Config.getInt(paramString)); }



   public Short getShort(String paramString) { return Short.valueOf((short)this.Config.getInt(paramString)); }



   public List<String> getLore(String paramString) { return this.Config.getStringList(paramString); }



   public FileConfiguration getConfig() { return this.Config; }
 }