package me.stephenminer.litecoin.util;

import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Profile {
    private final LiteCoin plugin;
    private final UUID uuid;
    private final String name;
    private int wins, total, gambleProfit;

    public Profile(UUID uuid, String gambleData){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
        this.uuid = uuid;
        this.name = plugin.nameFromUUID(uuid);
        String[] unbox = gambleData.split(",");
        this.wins = Integer.parseInt(unbox[0]);
        this.total = Integer.parseInt(unbox[1]);
        this.gambleProfit = Integer.parseInt(unbox[2]);
    }

    public Profile(UUID uuid, int wins, int totalGambles, int gambleProfit){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
        this.uuid = uuid;
        this.name = plugin.nameFromUUID(uuid);
        this.wins = wins;
        this.total = totalGambles;
        this.gambleProfit = gambleProfit;
    }


    public int wins(){ return wins; }
    public int totalGambles(){ return total; }
    public int gambleProfit(){ return gambleProfit; }

    /**
     * You should check if total > 0 first
     * @return ratio of gamble wins/losses
     */
    public float ratio(){
        if (total <= 0) return -1;
        else return ((float)wins) / total;
    }
    public String name(){ return name; }
    public UUID uuid(){ return uuid; }

    public void setWins(int wins){ this.wins = wins; }
    public void setTotalGambles(int total){ this.total = total; }
    public void setGambleProfit(int profit){ this.gambleProfit = profit; }

    public int losses(){
        return total - wins;
    }


    /**
     *  Gets data represntation of object to write to a file
     * @return String in format of "wins,total,gambleProfit"
     */
    public String dataString(){
        return wins + "," + total + "," + gambleProfit;
    }
}
