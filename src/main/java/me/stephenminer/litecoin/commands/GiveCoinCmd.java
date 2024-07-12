package me.stephenminer.litecoin.commands;

import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

/**
 * Command to manifest money from thin air to give to someone else
 */
public class GiveCoinCmd implements CommandExecutor {
    private final LiteCoin plugin;

    public GiveCoinCmd(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!sender.hasPermission("litecoin.commands.give")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return false;
        }
        if (args.length < 2){
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            sender.sendMessage(ChatColor.YELLOW + "Correct Usage: /givecoin [target-player] [coin-to-give]");
            sender.sendMessage(ChatColor.YELLOW + "[coin-to-give] can be negative");
            return false;
        }
        String target = args[0];
        int toGive = Integer.parseInt(args[1]);
        if (setBalances(target, toGive)){
            sender.sendMessage(ChatColor.GREEN + "Successfully set the balance of " + target + " to " + toGive);
            return true;
        }else{
            sender.sendMessage(ChatColor.RED + "Player " + target + " not found!");
            return false;
        }
    }


    private boolean setBalances(String target, int toGive){
        Player player = Bukkit.getPlayerExact(target);
        if (player == null){
            HashMap<String, UUID> names = plugin.namesOnFile();
            UUID uuid = names.get(target.toLowerCase());
            if (uuid != null){
                plugin.incrementBalance(uuid, toGive);
                return true;
            }else return false;
        }else{
            plugin.incrementBalance(player,toGive);
            return true;
        }

    }
}
