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
 * Command to check player balances
 */
public class BalCmd implements CommandExecutor {
    private final LiteCoin plugin;

    public BalCmd(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        // /bal for self
        if (args.length < 1){
            if (!sender.hasPermission("litecoin.commands.bal.self")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            if (!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED + "You need to be a player to use this command with no arguments!");
                sender.sendMessage(ChatColor.YELLOW + "For non-players: /bal [target-player]");
                return false;
            }
            Player player = (Player) sender;
            int bal = plugin.getBalance(player);
            player.sendMessage(ChatColor.GREEN + "Balance: " + bal + " LiteCoins");
            return true;
        }else{
            // /bal [player] for others
            if (!sender.hasPermission("litecoin.commands.bal.others")){
                sender.sendMessage(ChatColor.RED + "You do not have permission to view the balance of other players");
                return false;
            }
            String target = args[0];
            int balance = getBalance(target);
            if (balance == -1){
                sender.sendMessage(ChatColor.RED + "Player " + target + " not found");
                return false;
            }
            sender.sendMessage(ChatColor.GOLD + target + "'s" + " Balance: " + balance + " LiteCoins");
            return true;
        }

    }


    public int getBalance(String target){
        Player player = Bukkit.getPlayerExact(target);
        if (player == null){
            HashMap<String, UUID> names = plugin.namesOnFile();
            UUID uuid = names.getOrDefault(target.toLowerCase(), null);
            if (uuid != null) return plugin.getBalance(uuid);
            else return -1;
        }else return plugin.getBalance(player);
    }
}
