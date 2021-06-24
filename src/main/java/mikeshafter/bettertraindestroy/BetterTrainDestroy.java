package mikeshafter.bettertraindestroy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Statistic;
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public final class BetterTrainDestroy extends JavaPlugin implements CommandExecutor {
  boolean destroy;
  long warningDelay;
  BossBar bossBar;
  HashMap<Player, Queue<Integer>> statMap = new HashMap<>();
  final long fullWarningDelay = (getConfig().getLong("destroy") - getConfig().getLong("warning")) / 20;
  @Override public void onEnable() {
    getServer().getConsoleSender().sendMessage("§aTrainDestroy§b by Mineshafter61: v1.0");
    getConfig().options().copyDefaults(true);
    saveConfig();
    destroy = true;
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
        warningDelay = (getConfig().getLong("destroy") - getConfig().getLong("warning")) / 20;Bukkit.broadcastMessage(String.format("§b[§aTrainDestroy§b] §fTrains will be destroyed in %s seconds!", warningDelay));Bukkit.broadcastMessage("§b[§aTrainDestroy§b] §fIf you are currently riding a train, please get off at the next stop.");bossBar = Bukkit.createBossBar(String.format("§fTrains will be destroyed in %s seconds!", warningDelay), BarColor.BLUE, BarStyle.SOLID, BarFlag.CREATE_FOG);bossBar.removeFlag(BarFlag.CREATE_FOG);
    for (Player player: Bukkit.getOnlinePlayers()) {
      player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
      bossBar.addPlayer(player);
    }
        }, getConfig().getLong("warning"), getConfig().getLong("period"));
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
    for (Player player: Bukkit.getOnlinePlayers()) {
      player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.2f);
    }
        }, getConfig().getLong("warning") + 4, getConfig().getLong("period"));
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
    if (warningDelay > 0) {
      warningDelay--;
      bossBar.setTitle(String.format("§fTrains will be destroyed in %s seconds!", warningDelay));
      bossBar.setProgress((double) warningDelay / (double) fullWarningDelay);
    }
        }, 0, 20);
    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
    for (Player player: Bukkit.getOnlinePlayers()) bossBar.removePlayer(player);
    if (destroy) {
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
  @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("TrainDestroyDelay") && sender.hasPermission("TrainDestroy.delay")) {
      destroy = false;
      Bukkit.broadcastMessage("§b[§aTrainDestroy§b] §fTrainDestroy has been rescheduled!");
      return true;
    } else if (command.getName().equalsIgnoreCase("reload") && sender.hasPermission("TrainDestroy.delay")) {
      reloadConfig();
      saveConfig();
    } else if (command.getName().equalsIgnoreCase("odometer") && args.length == 1 && sender instanceof Player) {
      Player player = (Player) sender;
      if (args[0].equalsIgnoreCase("start")) {
        // start recording
        statMap.put(player, new LinkedList<>());
        statMap.get(player).add(player.getStatistic(Statistic.MINECART_ONE_CM));
        player.sendMessage(ChatColor.GREEN+""+player.getStatistic(Statistic.MINECART_ONE_CM));
        return true;
      } else if (args[0].equalsIgnoreCase("record")) {
        statMap.get(player).add(player.getStatistic(Statistic.MINECART_ONE_CM));
        player.sendMessage(ChatColor.GREEN+""+player.getStatistic(Statistic.MINECART_ONE_CM));
        return true;
      } else if (args[0].equalsIgnoreCase("stop")) {
        int first = statMap.get(player).remove();
        int i = 0;
        player.sendMessage(ChatColor.GREEN+"=== Results ===");
        player.sendMessage(ChatColor.YELLOW+""+i+" "+ChatColor.GREEN+"0");
        while (statMap.get(player).size() > 0) {
          ++i;
          int peeking = statMap.get(player).remove();
          player.sendMessage(ChatColor.YELLOW+""+i+" "+ChatColor.GREEN+(peeking-first)/100);
        } return true;
      }
    } return false;
  }
  @Override public void onDisable() {
    getServer().getConsoleSender().sendMessage("§cTrainDestroy§4 by Mineshafter61: Thank you for using!");
  }
}