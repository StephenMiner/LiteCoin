package me.stephenminer.litecoin.games.gui;

import me.stephenminer.litecoin.games.BlackJack;
import me.stephenminer.litecoin.games.Card;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BlackJackGui {

    private ItemStack filler, hit, stay;
    private Inventory inv;
    private BlackJack game;

    public BlackJackGui(BlackJack game){
        this.game = game;
        filler = filler();
        hit = hitButton();
        stay = stayButton();
        createInventory();
    }

    private void createInventory(){
        this.inv = Bukkit.createInventory(null, 45, "BlackJack Table");
        for (int i = 0; i < 9; i++) inv.setItem(i,filler);
        for (int i = 18; i < 27; i++) inv.setItem(i, filler);
        for (int i = 36; i < 45; i++) inv.setItem(i, filler);
       // updateInventory();
    }

    public void updateInventory(){
        for (int i = 10; i < 17; i++) inv.setItem(i,null);
        for (int i = 10; i < Math.min(10 + 7, 10 + game.dealerHand().size()); i++){
            Card card = game.dealerHand().get(i-10);
            inv.setItem(i, card(card));
        }

        for (int i = 28; i < 35; i++) inv.setItem(i,null);
        for (int i = 28; i < Math.min(28 + 7,28 + game.playerHand().size()); i++){
            Card card = game.playerHand().get(i - 28);
            inv.setItem(i, card(card));
        }
        if (game.ended()){
            inv.setItem(23,restartSameWager());
            inv.setItem(22, restartNewWager());
            inv.setItem(21, quit());
        }else {
            inv.setItem(23, playerScore());
            inv.setItem(22, hitButton());
            inv.setItem(21, stayButton());
        }
    }

    public void showPlayer(Player player){
        player.openInventory(inv);
    }

    public ItemStack card(Card card){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + card.display());
        List<String> lore = new ArrayList<>();
        String value;
        if (card.value() == 14) value = "1 or 11";
        else if (card.value() >= 10) value = "10";
        else value = "" + card.value();
        if (!card.faceDown())
            lore.add("Value: " + value);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack playerScore(){
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        int score = game.bestValue(false);
        meta.setDisplayName(ChatColor.YELLOW + "Current Score: " + score);
        List<String> lore = new ArrayList<>();
        lore.add("Can we get much higher?");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack hitButton(){
        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "HIT (draw card)");
        List<String> lore = new ArrayList<>();
        lore.add("We can get much higher");
        lore.add(ChatColor.BLACK + "hit-button");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack stayButton(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1, (short) 14);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "STAY");
        List<String> lore = new ArrayList<>();
        lore.add("We cannot get much higher");
        lore.add(ChatColor.BLACK + "stay-button");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    private ItemStack filler(){
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 7);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack restartNewWager(){
        ItemStack item = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Click to play with a new wager!");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Feeling Lucky?");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack restartSameWager(){
        ItemStack item = new ItemStack(Material.RECORD_6);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Click to play with the same Wager");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Like a broken record");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack quit(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "You sure you wanna quit?");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "You're quitting right before you make it big!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public Inventory inv(){ return inv; }
}
