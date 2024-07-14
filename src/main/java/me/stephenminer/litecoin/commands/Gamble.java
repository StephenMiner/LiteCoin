package me.stephenminer.litecoin.commands;

import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class Gamble implements CommandExecutor {
    private final LiteCoin plugin;

    public Gamble(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!sender.hasPermission("litecoin.commands.gamble")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return false;
        }
        if (args.length < 1){
            sender.sendMessage(ChatColor.RED + "Not enough arguments");
            sender.sendMessage(ChatColor.YELLOW + "Usage: /gamble [amount]");
        }
        if (sender instanceof Player){
            Player player = (Player) sender;
            int bal = plugin.getBalance(player);
            int bet;
            //Defining bet
            if (args[0].equalsIgnoreCase("all")){
                bet =bal;
            }else {
                try {
                    bet = Integer.parseInt(args[0]);
                }catch (Exception e){
                    sender.sendMessage(ChatColor.RED + "Invalid argument! Use either a whole number or 'all'");
                    return false;
                }
            }
            //You can't bet negative coins??
            if (bet < 0 || bal < bet){
                player.sendMessage(ChatColor.RED + "You don't have enough LiteCoin to make this bet!");
                return false;
            }

            if (gamble(player, bet)){
                player.sendMessage(ChatColor.GOLD + "You have won the gammble!");
            }else player.sendMessage(ChatColor.GOLD + "You may have lost, but who knows, you might win next time!");
            return true;
        }else sender.sendMessage(ChatColor.RED + "Only players can use this command!");
        return false;
    }


    private boolean gamble(Player player, int wager){
        Random random = new Random();
        int roll = random.nextInt(100);
        //Bet is won
        if (roll < 50) {
            plugin.incrementBalance(player,wager);
            return true;
        }else{
            //bet is lost
            plugin.incrementBalance(player, -1 * wager);
            return false;
        }
    }
}
