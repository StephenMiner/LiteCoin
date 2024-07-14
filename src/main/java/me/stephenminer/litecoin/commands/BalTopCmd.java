package me.stephenminer.litecoin.commands;

import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BalTopCmd implements CommandExecutor {
    private final LiteCoin plugin;

    public BalTopCmd(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!sender.hasPermission("litecoin.commands.baltop")){
            sender.sendMessage(ChatColor.RED + "No permission to use this command!");
            return false;
        }
        displayTop(sender);
        return true;
    }


    /**
     * Displays the top 3 (or less if 3 aren't present) players in terms of LiteCoin accumulation
     * @param sender who to display this information to
     */
    private void displayTop(CommandSender sender){
        sender.sendMessage(ChatColor.GOLD + "-------Top Coins-------");
        try {
            //get a sorted list of uuids based on their balances
            List<UUID> uuids = plugin.balances.keySet().stream()
                    .sorted((u1, u2) -> plugin.getBalance(u2) - plugin.getBalance(u1))
                    .collect(Collectors.toList());
            //display top 3
            int bound = Math.min(uuids.size(),3);
            for (int i = 0; i < bound; i++){
                UUID uuid = uuids.get(i);
                String name = plugin.nameFromUUID(uuid);
                sender.sendMessage((i+1) + ". " + name + ": " + plugin.getBalance(uuid) + " Lite Coins");
            }
        }catch (Exception ignored){}
    }
}
