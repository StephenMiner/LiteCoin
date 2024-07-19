package me.stephenminer.litecoin.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.stephenminer.litecoin.LiteCoin;
import me.stephenminer.litecoin.util.Profile;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GamblePlaceholder extends PlaceholderExpansion {
    private final LiteCoin plugin;
    public GamblePlaceholder(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }


    @Override
    public @NotNull String getIdentifier() {
        return "litegamble";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Stephenminer/Meep";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params){
        if (!params.isEmpty()){
            String[] unbox = params.split("_");
            if (unbox.length == 1) return processArgs(unbox[0], player.getUniqueId());
            if (unbox.length >= 2) return  processArgs(unbox[0], plugin.namesOnFile().get(unbox[1].toLowerCase()));
        }
        return "Bad arguments";
    }

    private String processArgs(String type, UUID target){
        Profile profile = plugin.profiles.getOrDefault(target, null);
        if (profile == null) return "Target Player Isn't Real";
        if (type.equalsIgnoreCase("wins")) return "" + profile.wins();
        if (type.equalsIgnoreCase("losses")) return "" + (profile.totalGambles()-profile.wins());
        if (type.equalsIgnoreCase("total")) return "" + profile.totalGambles();
        if (type.equalsIgnoreCase("profits")) return "" + profile.gambleProfit();
        if (type.equalsIgnoreCase("win-rate")) {
            double winRate = trimDouble(profile.ratio() * 100);
            if (winRate >= 0) return "" + winRate;
            else return "N/A";
        }
        return "Bad Arguments";
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
}
