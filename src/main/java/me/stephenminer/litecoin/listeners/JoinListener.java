package me.stephenminer.litecoin.listeners;

import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinListener implements Listener {
    private final LiteCoin plugin;
    public JoinListener(){
        this.plugin = JavaPlugin.getPlugin(LiteCoin.class);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (!plugin.balances.containsKey(player.getUniqueId())){
            plugin.balances.put(player.getUniqueId(),0);
        }
    }

}
