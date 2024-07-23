package me.stephenminer.litecoin.listeners;

import me.stephenminer.litecoin.LiteCoin;
import me.stephenminer.litecoin.commands.BlackJackCmd;
import me.stephenminer.litecoin.games.BlackJack;
import me.stephenminer.litecoin.games.gui.BlackJackGui;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GuiListener implements Listener {
    private final LiteCoin plugin;
    private Set<UUID> gameTimeout;

    private Set<UUID> tellingWager;

    public GuiListener(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
        this.gameTimeout = new HashSet<>();
        tellingWager = new HashSet<>();

    }



    @EventHandler
    public void blackjackGUIClicks(InventoryClickEvent event){
        Player player =(Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        BlackJack game = BlackJackCmd.activeGames.getOrDefault(uuid,null);
        if (game == null) return;
        Inventory top = event.getView().getTopInventory();
        if (top != null && game.gui().inv().equals(top)){
            event.setCancelled(true);
            int slot = event.getSlot();
            if (game.ended()) {
                if (slot == 21) player.closeInventory();
                else if (slot == 22) {
                    BlackJackCmd.activeGames.remove(uuid);
                    tellingWager.add(uuid);
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "Type out your new wager in chat!");
                }else{
                    game.restart();
                }
            }else {
                BlackJack.Result result = null;
                if (slot == 21) result = game.stayPlayer();
                else if (slot == 22) result = game.hitPlayer();
                if (result != null) resultMessage(result, player);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        BlackJack game = BlackJackCmd.activeGames.getOrDefault(player.getUniqueId(), null);
        if (game == null) return;
        if (game.ended()){
            BlackJackCmd.activeGames.remove(player.getUniqueId());
            gameTimeout.remove(player.getUniqueId());
            return;
        }
        if (gameTimeout.contains(player.getUniqueId())) return;
        gameTimeout.add(player.getUniqueId());
        startTimeOut(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "Make sure to type /blackjack continue to continue your game!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (gameTimeout.contains(event.getPlayer().getUniqueId())){
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Make sure to type /blackjack continue to continue your game!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        tellingWager.remove(player.getUniqueId());
        BlackJack game = BlackJackCmd.activeGames.getOrDefault(player.getUniqueId(), null);
        if (game == null) return;
        if (game.ended()){
            BlackJackCmd.activeGames.remove(player.getUniqueId());
            return;
        }
        if (gameTimeout.contains(player.getUniqueId())) return;
        gameTimeout.add(player.getUniqueId());
        startTimeOut(player.getUniqueId());
    }



    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!tellingWager.contains(uuid)) return;
        event.setCancelled(true);
        String msg = event.getMessage();
        tellingWager.remove(uuid);
        int wager;
        try{
            wager = Integer.parseInt(msg);
        }catch (Exception ignored){
            Bukkit.getScheduler().runTaskLater(plugin,()->player.sendMessage(ChatColor.RED + "Cancelling new blackjack game, inputted wager is not a number!"),2);
            return;
        }
        int bal = plugin.getBalance(player);
        if (bal < wager){
            Bukkit.getScheduler().runTaskLater(plugin,()->player.sendMessage(ChatColor.RED + "You dont have enough LiteCoin to make this wager"),2);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin,()->Bukkit.dispatchCommand(player,"blackjack " + wager),2);

    }

    private void startTimeOut(UUID uuid){
        BlackJack game = BlackJackCmd.activeGames.getOrDefault(uuid,null);
        new BukkitRunnable(){
            int max = 5*60*20;
            int count = 0;
            @Override
            public void run(){
                if (!gameTimeout.contains(uuid) || game==null || game.ended()){
                    gameTimeout.remove(uuid);
                    this.cancel();
                    return;
                }
                if (count >= max){
                    this.cancel();
                    if (Bukkit.getPlayer(uuid).isOnline()){
                        BlackJack game = BlackJackCmd.activeGames.getOrDefault(uuid,null);
                        if (game != null){
                            game.payout(BlackJack.Result.LOSE,false);
                            BlackJackCmd.activeGames.remove(uuid);
                        }
                        gameTimeout.remove(uuid);
                        Player p = Bukkit.getPlayer(uuid).getPlayer();
                        p.sendMessage(ChatColor.RED + "You lose your blackjack game by default for quitting for too long!");
                    }
                }
                count++;
            }

        }.runTaskTimer(plugin, 0,1);
    }

    public void resultMessage(BlackJack.Result result, Player player){
        Location loc = player.getLocation();
        switch (result){
            case WIN:
               // BlackJackCmd.activeGames.remove(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You've won!");
                player.playSound(loc, Sound.LEVEL_UP,2,1);
            //    player.closeInventory();

                break;
            case LOSE:
            case BUST:
              //  BlackJackCmd.activeGames.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You've lost your wager!");
                player.playSound(loc,Sound.CAT_MEOW,2,1);
              //  player.closeInventory();

                break;
            case CONTINUE:
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "You're safe to go higher... for now");
                break;
            case PUSH:
                player.sendMessage(ChatColor.YELLOW + "It's a tie!");
        }

    }
}
