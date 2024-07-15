package me.stephenminer.litecoin.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        if (!params.isEmpty()) {
            String[] split = params.split("_");
            if (split.length == 1) {
                try {
                    return ranking(Integer.parseInt(split[0]));
                }catch (Exception ignored){
                    return "integer not found";
                }
            }
            return "N/A";
        }else return "" + plugin.getBalance(player);
    }

    public String ranking(int index){
        //get a sorted list of uuids based on their balances
        List<UUID> uuids = plugin.balances.keySet().stream()
                .sorted((u1, u2) -> plugin.getBalance(u2) - plugin.getBalance(u1))
                .collect(Collectors.toList());
        if (uuids.size() > index-1) {
            String name = plugin.nameFromUUID(uuids.get(index-1));
            return name + ": " + plugin.getBalance(uuids.get(index-1));
        }
        return "N/A";
    }
}
