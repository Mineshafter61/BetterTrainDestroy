package mikeshafter.bettertraindestroy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class BetterTrainDestroy extends JavaPlugin {
  
  boolean destroy;
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    
    // TODO: destroy
    destroy = true;
    
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
      public void run() {
        Bukkit.broadcastMessage(ChatColor.GREEN+"§b[§aTrainDestroy§b] §fTrains will be destroyed in 3 minutes!");
        Bukkit.broadcastMessage(ChatColor.GREEN+"§b[§aTrainDestroy§b] §fIf you are currently riding a train, please get off at the next stop.");
        for (Player player : Bukkit.getOnlinePlayers()) {
          player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.5f);
        }
      }
    }, 72000, 216000);
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
      public void run(){
        for (Player player : Bukkit.getOnlinePlayers()) {
          player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.2f);
        }
      }
    }, 72004, 216000);
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
      public void run(){
        if (destroy){
          ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
          Bukkit.broadcastMessage(ChatColor.GREEN+"§b[§aTrainDestroy§b] §fTrains have been destroyed!");
          Bukkit.dispatchCommand(console, "train destroyall");
          Bukkit.dispatchCommand(console, "ekillall minecarts world");
        } else {
          destroy = true;
          Bukkit.broadcastMessage(ChatColor.GREEN+"§b[§aICIWI§b] §fTrainDestroy rescheduled, trains will be destroyed in the next cycle.");
        }}}, 73200, 216000);
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
  

}
