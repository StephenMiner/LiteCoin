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

public class SetBalCmd implements CommandExecutor {
    private final LiteCoin plugin;

    public SetBalCmd(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!sender.hasPermission("litecoin.commands.setbal")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return false;
        }
        if (args.length < 2){
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            sender.sendMessage(ChatColor.YELLOW + "Correct usage: " + "/setbal [player-name] [new-balance]");
            return false;
        }
        String target = args[0];
        int balance = Integer.parseInt(args[1]);
        if (!setBalance(target, balance)){
            sender.sendMessage(ChatColor.RED + "Could not find player " + target);
            return false;
        }else{
            sender.sendMessage(ChatColor.GREEN + "Set the balance of " + target + " to " + balance);
            return true;
        }
    }

    private boolean setBalance(String target, int balance){
        Player player = Bukkit.getPlayerExact(target);
        if (player == null){
            HashMap<String, UUID> names = plugin.namesOnFile();
            UUID uuid = names.getOrDefault(target.toLowerCase(),null);
            if (uuid!=null){
                plugin.setBalance(uuid, balance);
                return true;
            }else return false;
        }else{
            plugin.setBalance(player,balance);
            return true;
        }
    }

}
