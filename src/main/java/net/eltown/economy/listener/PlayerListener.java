package net.eltown.economy.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.eltown.economy.Economy;

public class PlayerListener implements Listener {

    private final Economy plugin;

    public PlayerListener(Economy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Economy.getAPI().hasAccount(event.getPlayer(), has -> {
            if (!has) Economy.getAPI().createAccount(event.getPlayer(), this.plugin.getDefaultMoney());
        });
    }

}
