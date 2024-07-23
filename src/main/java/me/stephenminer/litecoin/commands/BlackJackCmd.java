package me.stephenminer.litecoin.commands;

import me.stephenminer.litecoin.LiteCoin;
import me.stephenminer.litecoin.games.BlackJack;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class BlackJackCmd implements CommandExecutor {
    public static HashMap<UUID, BlackJack> activeGames = new HashMap<>();
    private final LiteCoin plugin;

    public BlackJackCmd(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (!player.hasPermission("litecoin.commands.blackjack")){
                player.sendMessage(ChatColor.RED + "No permission to use this command!");
                return false;
            }

            if (activeGames.containsKey(player.getUniqueId())){
                player.sendMessage(ChatColor.YELLOW + "Loading saved game");
                BlackJack game = activeGames.get(player.getUniqueId());
                game.gui().showPlayer(player);
                return true;
            }

            if (args.length < 1){
                player.sendMessage(ChatColor.RED + "Not enough arguments!");
                player.sendMessage(ChatColor.YELLOW + "/blackjack [whole-number-wager]");
                return false;
            }
            int wager;
            try{
                wager = Integer.parseInt(args[0]);
            }catch (Exception ignored){
                sender.sendMessage(ChatColor.RED + "Input " + args[0] + " is not a whole number, but it should be!");
                return false;
            }
            if (wager == 0){
                player.sendMessage(ChatColor.RED + "No reason to bet 0 litecoins! Waste of time!");
                return false;
            }
            if (wager < plugin.minBlackJackBet()){
                player.sendMessage(ChatColor.RED + "You must bet more than " + plugin.minBlackJackBet() + " LiteCoins for this game");
                return false;
            }
            int balance = plugin.getBalance(player);

            if (balance < wager){
                player.sendMessage(ChatColor.RED + "You do not have enough litecoin to make this wager");
                return false;
            }

            BlackJack blackJack = new BlackJack(player,wager);
            blackJack.startGame();
            activeGames.put(player.getUniqueId(),blackJack);
            player.sendMessage(ChatColor.GREEN + "Starting game");
            return true;

        }else sender.sendMessage(ChatColor.RED + "You need to be a player to use this command!");
        return false;
    }
}
