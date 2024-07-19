package me.stephenminer.litecoin.listeners;

import me.stephenminer.litecoin.LiteCoin;
import me.stephenminer.litecoin.util.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class JoinListener implements Listener {
    private final LiteCoin plugin;
    public JoinListener(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!plugin.balances.containsKey(uuid)){
            plugin.balances.put(player.getUniqueId(),0);
        }
        if (!plugin.profiles.containsKey(uuid)){
            plugin.profiles.put(uuid, new Profile(uuid, 0,0,0));
        }
    }

}
