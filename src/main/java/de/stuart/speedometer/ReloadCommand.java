package de.stuart.speedometer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

import static de.stuart.speedometer.Main.log;

public class ReloadCommand implements CommandExecutor {
    
    private static Main plugin = Main.instance;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof ConsoleCommandSender){
            plugin.reloadConfig();
            log.log(Level.INFO, "Reloaded the config!");
        } else if (sender instanceof Player){
            Player player = (Player) sender;
            
            if(player.hasPermission("speedometer.reload")){
                plugin.reloadConfig();
                player.sendMessage("§8| §6Speedometer §7: Reloaded the config!");
            }
        }
        return true;
    }
}
