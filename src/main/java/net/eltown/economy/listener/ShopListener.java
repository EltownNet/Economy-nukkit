package net.eltown.economy.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import lombok.RequiredArgsConstructor;
import net.eltown.economy.Economy;

@RequiredArgsConstructor
public class ShopListener implements Listener {

    private final Economy economy;

    @EventHandler
    public void on(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        if (event.getEntity().namedTag.exist("npc_id")) {
            switch (event.getEntity().namedTag.getString("npc_id")) {
                case "servercore:lumberjack":
                    this.economy.getShopAPI().getShopForms().openWoodShop(player);
                    break;
                case "servercore:mining":
                    this.economy.getShopAPI().getShopForms().openMiningShop(player);
                    break;
                case "servercore:nature":
                    this.economy.getShopAPI().getShopForms().openExploringShop(player);
                    break;
                case "servercore:nether":
                    this.economy.getShopAPI().getShopForms().openNetherShop(player);
                    break;
                case "servercore:mobdrops":
                    this.economy.getShopAPI().getShopForms().openMobdropShop(player);
                    break;
            }
        }
    }

}
