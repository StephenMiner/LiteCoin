package me.stephenminer.litecoin.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankPlaceholder extends PlaceholderExpansion {
    private final int index;
    private final LiteCoin plugin;
    public RankPlaceholder(int index){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
        this.index = index;
    }







    public String ranking(){
        //get a sorted list of uuids based on their balances
        List<UUID> uuids = plugin.balances.keySet().stream()
                .sorted((u1, u2) -> plugin.getBalance(u2) - plugin.getBalance(u1))
                .collect(Collectors.toList());
        return plugin.nameFromUUID(uuids.get(index-1));
    }



    @Override
    public @NotNull String getIdentifier() {
        return "litecoin_" + index;
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
        return ranking();
    }
}
