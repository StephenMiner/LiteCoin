package me.stephenminer.litecoin.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CoinPlaceholder extends PlaceholderExpansion {
    private final LiteCoin plugin;
    public CoinPlaceholder(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }
    @Override
    public @NotNull String getIdentifier() {
        return "litecoin";
    }

    @Override
    public @NotNull String getAuthor() {
        return "StephenminerMeep";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params){
        return "" + plugin.getBalance(player);
    }
}
