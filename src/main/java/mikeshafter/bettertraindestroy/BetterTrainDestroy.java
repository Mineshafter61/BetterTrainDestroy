package mikeshafter.bettertraindestroy;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class BetterTrainDestroy extends JavaPlugin implements CommandExecutor {
  
  boolean destroy;
  final long fullWarningDelay = (getConfig().getLong("destroy") - getConfig().getLong("warning")) / 20;
  long warningDelay;
  BossBar bossBar;
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    getServer().getConsoleSender().sendMessage("§aTrainDestroy§b by Mineshafter61: v1.0");
    getConfig().options().copyDefaults(true);
    saveConfig();
    
    // TODO: destroy
    destroy = true;
    
    // 3 min warning
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
      warningDelay = (getConfig().getLong("destroy") - getConfig().getLong("warning")) / 20;
      Bukkit.broadcastMessage(String.format("§b[§aTrainDestroy§b] §fTrains will be destroyed in %s seconds!", warningDelay ));
      Bukkit.broadcastMessage("§b[§aTrainDestroy§b] §fIf you are currently riding a train, please get off at the next stop.");
      bossBar = Bukkit.createBossBar(String.format("§fTrains will be destroyed in %s seconds!", warningDelay), BarColor.BLUE, BarStyle.SOLID, BarFlag.CREATE_FOG);
      bossBar.removeFlag(BarFlag.CREATE_FOG);
      for (Player player : Bukkit.getOnlinePlayers()) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.5f);
        bossBar.addPlayer(player);
      }
    }, getConfig().getLong("warning"), getConfig().getLong("period"));
    
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
      for (Player player : Bukkit.getOnlinePlayers()) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.2f);
      }
    }, getConfig().getLong("warning")+4, getConfig().getLong("period"));
    
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
      if (warningDelay > 0) {
        warningDelay--;
        bossBar.setTitle(String.format("§fTrains will be destroyed in %s seconds!", warningDelay));
        bossBar.setProgress((double) warningDelay/(double) fullWarningDelay);
      }
    }, 0, 20);
    
    // Destroyer
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
      for (Player player : Bukkit.getOnlinePlayers()) bossBar.removePlayer(player);
      if (destroy){
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.broadcastMessage("§b[§aTrainDestroy§b] §fTrains have been destroyed!");
        Bukkit.dispatchCommand(console, "train destroyall");
        Bukkit.dispatchCommand(console, "ekillall minecarts world");
      } else {
        destroy = true;
        Bukkit.broadcastMessage("§b[§aTrainDestroy§b] §fTrainDestroy rescheduled, trains will be destroyed in the next cycle.");
      }
    }, getConfig().getLong("destroy"), getConfig().getLong("period"));
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("TrainDestroyDelay") && sender.hasPermission("TrainDestroy.delay")) {
      destroy = false;
      Bukkit.broadcastMessage("§b[§aTrainDestroy§b] §fTrainDestroy has been rescheduled!");
      return true;
    }
    
    else if (command.getName().equalsIgnoreCase("reload") && sender.hasPermission("TrainDestroy.delay")) {
      reloadConfig();
      saveConfig();
    }
    return false;
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    getServer().getConsoleSender().sendMessage("§cTrainDestroy§4 by Mineshafter61: Thank you for using!");
  }
}
