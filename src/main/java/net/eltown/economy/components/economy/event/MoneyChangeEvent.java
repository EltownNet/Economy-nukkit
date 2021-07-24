package net.eltown.economy.components.economy.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.Getter;

public class MoneyChangeEvent extends PlayerEvent {

    @Getter
    private final double money;
    private static final HandlerList handlers = new HandlerList();

    public MoneyChangeEvent(final Player player, final double money) {
        this.player = player;
        this.money = money;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
