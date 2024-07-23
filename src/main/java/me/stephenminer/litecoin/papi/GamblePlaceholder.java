package me.stephenminer.litecoin.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.stephenminer.litecoin.LiteCoin;
import me.stephenminer.litecoin.util.Profile;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
           // %litegamble_wins%
            if (unbox.length == 1) return processArgs(unbox[0], player.getUniqueId());
            // %litegameble_wins_Stephenminer%
            if (unbox.length == 2) return  processArgs(unbox[0], plugin.namesOnFile().get(unbox[1].toLowerCase()));
            if (unbox.length == 3){
                //%litegamble_wins_rank_2%
                if (unbox[1].equalsIgnoreCase("rank"))
                    return processRankArgs(unbox[0],unbox[2]);
            }
            if (unbox.length >=4){
                //%litegamble_wins_rank_1_val%
                if (unbox[1].equalsIgnoreCase("rank") && unbox[3].equalsIgnoreCase("val"))
                    return processRankValue(unbox[0],unbox[2]);
            }
        }
        return "Bad arguments";
    }

    private String processArgs(String type, UUID target){
        Profile profile = plugin.profiles.getOrDefault(target, null);
        {
            if (type.equalsIgnoreCase("wins")) return "" + profile.wins();
            if (type.equalsIgnoreCase("losses")) return "" + (profile.totalGambles() - profile.wins());
            if (type.equalsIgnoreCase("total")) return "" + profile.totalGambles();
            if (type.equalsIgnoreCase("profits")) return "" + profile.gambleProfit();
            if (type.equalsIgnoreCase("win-rate")) {
                double winRate = trimDouble(profile.ratio() * 100);
                if (winRate >= 0) return "" + winRate;
                else return "N/A";
            }
        }
        return "Bad Arguments";
    }

    private String processRankArgs(String type, String arg){
        int rank;
        try{
            rank = Integer.parseInt(arg);
        }catch (Exception ignored){
            return arg + " needs to be a whole number";
        }
        Profile profile = rankingProfile(type.toLowerCase(),rank);
        if (profile == null) return "N/A";
        else return profile.name();
    }
    private String processRankValue(String type, String arg){
        int rank;
        try{
            rank = Integer.parseInt(arg);
        }catch (Exception ignored){
            return arg + " needs to be a whole number";
        }
        Profile profile = rankingProfile(type.toLowerCase(),rank);
        if (profile == null) return "N/A";
        switch (type.toLowerCase()){
            case "wins":
                return "" + profile.wins();
            case "losses":
                return "" + profile.losses();
            case "total":
                return "" + profile.totalGambles();
            case "profits":
                return "" + profile.gambleProfit();
            case "win-rate":
                return (100 * trimDouble(profile.ratio())) + "%";
        }
        return "N/A";
    }

    public Profile rankingProfile(String metric, int index){
        Stream<Profile> profileStream = plugin.profiles.values().stream();
        List<Profile> sorted;
        switch (metric){
            case "wins":
                sorted = profileStream.sorted((p1,p2)->p2.wins()-p1.wins()).collect(Collectors.toList());
                break;
            case "losses":
                sorted = profileStream.sorted((p1,p2)->p2.losses() - p1.losses()).collect(Collectors.toList());
                break;
            case "total":
                sorted = profileStream.sorted((p1,p2)->p2.totalGambles() - p1.totalGambles()).collect(Collectors.toList());
                break;
            case "profits":
                sorted = profileStream.sorted((p1,p2)->p2.gambleProfit()-p1.gambleProfit()).collect(Collectors.toList());
                break;
            case "win-rate":
                sorted = profileStream.sorted((p1,p2)->(int)((100*p2.ratio())-(100*p1.ratio()))).collect(Collectors.toList());
                break;
            default:
                return null;
        }
        System.out.println(sorted.size());
        System.out.println(index);
        if (sorted.size() > index-1) return sorted.get(index-1);
        else return null;
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
