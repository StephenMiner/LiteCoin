package me.stephenminer.litecoin.commands;

import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Command for money transfers
 */

public class PayCmd implements CommandExecutor {
    private final LiteCoin plugin;

    public PayCmd(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!sender.hasPermission("litecoin.commands.pay")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return false;
        }
        if (!(sender instanceof  Player)){
            sender.sendMessage(ChatColor.RED + "You need to be a player to use this command");
            return false;
        }
        if (args.length < 2){
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            sender.sendMessage(ChatColor.YELLOW + "Correct usage: /pay [player-name] [amount-you-want-to-give]");
            return false;
        }
        Player player = (Player) sender;

        int toPay = Integer.parseInt(args[1]);
        if (toPay < 1){
            player.sendMessage(ChatColor.RED + "There is no reason to send 0 currency");
            return false;
        }
        if (!canTransfer(player,toPay)){
            player.sendMessage(ChatColor.RED + "You do not have enough litecoin to make this transaction!");
            return false;
        }
        String targetName = args[0];
       if (!updateBalances(player, targetName, toPay)){
           player.sendMessage(ChatColor.RED + "Could not find player " + targetName);
           return false;
       }
        return true;
    }

    public boolean updateBalances(OfflinePlayer sender, String target, int toPay){
        Player player = Bukkit.getPlayerExact(target);
        if (player != null){
            plugin.incrementBalance(sender,-1*toPay);
            plugin.incrementBalance(player.getUniqueId(),toPay);
            return true;
        }else{
            HashMap<String, UUID> names = plugin.namesOnFile();
            UUID uuid = names.getOrDefault(target.toLowerCase(), null);
            if (uuid != null){
                plugin.incrementBalance(sender, -1*toPay);
                plugin.incrementBalance(uuid, toPay);
                return true;
            }
        }
        return false;
    }

    /**
     * Makes sure the sender has enough money to pay the target
     * @param sender player to check the balance of
     * @param toPay the amount of money the player needs to have
     * @return true if sender's balance >= toPay, false otherwise
     */
    private boolean canTransfer(Player sender, int toPay){
        return plugin.getBalance(sender) >= toPay;
    }


}
