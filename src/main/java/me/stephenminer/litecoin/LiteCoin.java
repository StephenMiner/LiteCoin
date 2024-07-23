package me.stephenminer.litecoin;

import me.stephenminer.litecoin.commands.*;
import me.stephenminer.litecoin.listeners.GuiListener;
import me.stephenminer.litecoin.listeners.JoinListener;
import me.stephenminer.litecoin.papi.CoinPlaceholder;
import me.stephenminer.litecoin.papi.GamblePlaceholder;
import me.stephenminer.litecoin.papi.RankPlaceholder;
import me.stephenminer.litecoin.util.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class LiteCoin extends JavaPlugin {
    public ConfigFile playerFile;
    public ConfigFile settings;
    public HashMap<UUID, Integer> balances;
    public HashMap<UUID, Profile> profiles;

    @Override
    public void onEnable() {
        playerFile = new ConfigFile(this,"players");
        this.settings = new ConfigFile(this, "settings");
        balances = new HashMap<>();
        profiles = new HashMap<>();
        portBalances();
        loadBalances();

        loadProfiles();
        addCommands();
        registerEvents();
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            boolean enabled = new CoinPlaceholder().register();
            new GamblePlaceholder().register();
            this.getLogger().info("LiteCoin placeholder expansion enabled: " + enabled);
        }
    }

    @Override
    public void onDisable() {
        saveBals();
        saveProfiles();
    }

    private void addCommands(){
        getCommand("pay").setExecutor(new PayCmd());
        getCommand("givecoin").setExecutor(new GiveCoinCmd());
        getCommand("bal").setExecutor(new BalCmd());
        getCommand("setbal").setExecutor(new SetBalCmd());
        getCommand("gamble").setExecutor(new Gamble());
        getCommand("baltop").setExecutor(new BalTopCmd());
        getCommand("blackjack").setExecutor(new BlackJackCmd());
    }

    private void registerEvents(){
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new JoinListener(),this);
        pm.registerEvents(new GuiListener(),this);
    }
    private void portBalances(){
        if (!this.playerFile.getConfig().contains("players")) return;
        Set<String> uuids = this.playerFile.getConfig().getConfigurationSection("players").getKeys(false);
        for (String sUUID : uuids){
            String path = "balances." + sUUID + ".bal";
            int bal = this.playerFile.getConfig().getInt("players." + sUUID);
            this.playerFile.getConfig().set(path, bal);
        }
        this.playerFile.getConfig().set("players",null);
        this.playerFile.saveConfig();
    }
    /**
     * Saves player balances to players.yml
     * format -> UUID: [balance]
     */
    public void saveBals(){
        Set<UUID> keys = balances.keySet();
        for (UUID uuid : keys){
            int bal = balances.get(uuid);
            this.playerFile.getConfig().set("balances." + uuid.toString() + ".bal", bal);
        }
        this.playerFile.saveConfig();
    }

    public void loadBalances(){
        if (!this.playerFile.getConfig().contains("balances"))return;
        Set<String> uuids = this.playerFile.getConfig().getConfigurationSection("balances").getKeys(false);
        for (String sUUID : uuids){
            UUID uuid = UUID.fromString(sUUID);
            int bal = this.playerFile.getConfig().getInt("balances." + sUUID + ".bal");
            this.balances.put(uuid, bal);
        }
    }

    public void loadProfiles(){
        if (!this.playerFile.getConfig().contains("balances")) return;
        Set<String> uuids = this.playerFile.getConfig().getConfigurationSection("balances").getKeys(false);
        for (String sUUID : uuids){
            UUID uuid = UUID.fromString(sUUID);
            String gambleData = this.playerFile.getConfig().getString("balances." + sUUID + ".gambles");
            Profile profile = gambleData != null ? new Profile(uuid, gambleData) : new Profile(uuid, 0,0,0);
            profiles.put(uuid, profile);
        }
    }
    public void saveProfiles(){
        for (Profile profile : profiles.values()){
            String path = "balances." + profile.uuid().toString() + ".gambles";
            this.playerFile.getConfig().set(path,profile.dataString());
            this.playerFile.saveConfig();
        }
    }

    /**
     *
     * @param player
     * @return the balance of the inputted player, default = 0
     */
    public int getBalance(OfflinePlayer player){
        return balances.getOrDefault(player.getUniqueId(),0);
    }

    /**
     *
     * @param uuid
     * @return the balance of the inputted player, default = 0
     */
    public int getBalance(UUID uuid){
        return balances.getOrDefault(uuid,0);
    }

    /**
     * Sets the balance of the inputted player
     * @param player player to set the balance of
     * @param bal value to set the balance to
     */
    public void setBalance(OfflinePlayer player, int bal ){
        setBalance(player.getUniqueId(), bal);
        if (player.isOnline()){
            player.getPlayer().sendMessage(ChatColor.GOLD + "Your balance has been set to " + bal + " LiteCoins");
        }
    }

    /**
     * Sets the balance of the inputted player
     * Will not set a balance to be < 0
     * @param uuid uuid to set balance of
     * @param bal value to set the balance to
     */
    public void setBalance(UUID uuid, int bal){
        bal = Math.max(0, bal);
        balances.put(uuid, bal);
    }

    /**
     * Increments the players balance
     * @param player player to increment the balance of
     * @param increment the amount to modify the balance by
     */
    public void incrementBalance(OfflinePlayer player, int increment){
        incrementBalance(player.getUniqueId(),increment);

        if (player.isOnline()) {
            String msg = increment > 0 ? "+" + increment + " LiteCoin" : increment + " LiteCoin";
            player.getPlayer().sendMessage(ChatColor.GOLD + msg);
        }
    }

    /**
     * Increments the players balance
     * @param uuid uuid to increment the balance of
     * @param increment the amount to modify the balance by
     */
    public void incrementBalance(UUID uuid, int increment){
        int oldBal = getBalance(uuid);
        balances.put(uuid, oldBal + increment);
    }
    /**
     * For tabcompletion things
     * @param base the String collection to check against match
     * @param match the String that all base contents will be checked against
     * @return List of Strings from base that contain the match parameter regardless of case
     */
    public List<String> filter(Collection<String> base, String match){
        match = match.toLowerCase();
        List<String> filtered = new ArrayList<>();
        for (String entry : base){
            String temp = ChatColor.stripColor(entry).toLowerCase();
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }

    /**
     *Attempts to get a playername from a UUID by first getting an OfflinePlayer
     * @param uuid the UUID to get a name for
     * @return the name of the OfflinePlayer whose UUID is uuid, null if an OfflinePlayer isn't found
     */
    public String nameFromUUID(UUID uuid){
        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        if (offline != null) return offline.getName();
        else return null;
    }

    /**
     *
     * @return HashMap of player names from UUIDS in the balances HashMap. Player names are lower cased
     */
    public HashMap<String, UUID> namesOnFile(){
        HashMap<String, UUID> namesOnFile = new HashMap<>();
        Set<UUID> uuids = balances.keySet();
        for (UUID uuid : uuids){
            String name = nameFromUUID(uuid);
            if (name != null) namesOnFile.put(name.toLowerCase(), uuid);
        }
        return namesOnFile;
    }

    public int minBlackJackBet(){
        return this.settings.getConfig().getInt("blackjack.min-bet");
    }


}
