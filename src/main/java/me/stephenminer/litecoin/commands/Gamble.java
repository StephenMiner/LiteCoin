package me.stephenminer.litecoin.commands;

import me.stephenminer.litecoin.LiteCoin;
import me.stephenminer.litecoin.util.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Gamble implements CommandExecutor {
    private final LiteCoin plugin;

    private HashMap<UUID, Long> cooldown;

    public Gamble(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
        cooldown = new HashMap<>();
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
            sender.sendMessage(ChatColor.YELLOW + "Usage: /gamble all");
            sender.sendMessage(ChatColor.YELLOW + "Usage: /gamble stats");
            return false;
        }
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("stats")){
                displayStats(player, player.getUniqueId());
                return true;
            }
            //cooldown check
            long stored = cooldown.getOrDefault(player.getUniqueId(), 0L);
            if (stored > System.currentTimeMillis()){
                double timeLeft = (stored - System.currentTimeMillis()) / 1000d / 60d;

                player.sendMessage(ChatColor.RED + "Command on cooldown!");
                player.sendMessage(ChatColor.RED + "" + trimDouble(timeLeft) + " minutes until you can cast this command again!");
                return false;
            }
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
            boolean betAll = bet == bal;
            if (gamble(player, bet)){
                player.sendMessage(ChatColor.GOLD + "You have won the gamble!");
            }else {
                player.sendMessage(ChatColor.GOLD + "You may have lost, but who knows, you might win next time!");
                if (betAll)
                    Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " has gambled away all " + bet + " of their LiteCoins and now have 0! So sad!");
            }
            long cdMillis = (long) (readCooldown() * 60 * 1000);
            cooldown.put(player.getUniqueId(), System.currentTimeMillis() + cdMillis);
            return true;
        }else sender.sendMessage(ChatColor.RED + "Only players can use this command!");
        return false;
    }



    private boolean gamble(Player player, int wager){
        Random random = new Random();
        int roll = random.nextInt(100);
        int chance = (int) (readChance() * 100);
        Profile profile = plugin.profiles.get(player.getUniqueId());
        profile.setTotalGambles(profile.totalGambles() + 1);
        //Bet is won
        if (roll < chance) {
            plugin.incrementBalance(player,wager);
            profile.setGambleProfit(profile.gambleProfit() + wager);
            profile.setWins(profile.wins() + 1);
            return true;
        }else{
            //bet is lost
            plugin.incrementBalance(player, -1 * wager);
            profile.setGambleProfit(profile.gambleProfit() - wager);
            return false;
        }
    }

    /**
     * Gets the chance to win a gamble from file
     * @return chance to win a gamble as a double
     */
    private double readChance(){
        String path = "gamble.chance";
        if (plugin.settings.getConfig().contains(path))
            return plugin.settings.getConfig().getDouble(path);
        else return 0.5;
    }

    /**
     * Gets the cooldown for gamble cmd from settings.yml file
     * @return cooldown for gamble cmd in minutes
     */
    private double readCooldown(){
        String path = "gamble.cooldown";
        if (plugin.settings.getConfig().contains(path))
            return plugin.settings.getConfig().getDouble(path);
        else return 10;
    }

    /**
     * Removes all decimal places after the 2nd
     * @param d input
     * @return double from d with only 2 decimal places
     */
    private double trimDouble(double d){
        int convert = (int) (d * 100);
        return convert / 100d;
    }

    private void displayStats(CommandSender sender, UUID target){
        Profile profile = plugin.profiles.get(target);
        sender.sendMessage(ChatColor.GOLD + "------Gamble Stats------");
        sender.sendMessage("Wins: " + profile.wins());
        sender.sendMessage("Losses: " + (profile.totalGambles() - profile.wins()));
        double winRate = trimDouble(profile.ratio()*100);
        if (winRate >= 0) sender.sendMessage("Win Rate: " + winRate);
        else sender.sendMessage("Win Rate: N/A");
        sender.sendMessage("Gambling Profits: " + profile.gambleProfit());
    }


}
