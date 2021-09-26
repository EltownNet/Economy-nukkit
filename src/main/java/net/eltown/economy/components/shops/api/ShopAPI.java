package net.eltown.economy.components.shops.api;

import lombok.Getter;
import lombok.SneakyThrows;
import net.eltown.economy.Economy;
import net.eltown.economy.components.shops.data.ShopCalls;

import java.util.function.Consumer;

@Getter
public class ShopAPI {

    private final Economy economy;

    @SneakyThrows
    public ShopAPI(final Economy economy) {
        this.economy = economy;
    }

    public void getCurrentPrice(final int[] id, final int amount, final Consumer<Double> callback) {
        this.economy.getRabbit().sendAndReceive((delivery) -> {
            final double price = Double.parseDouble(delivery.getData()[1]);
            callback.accept(price * amount);
        }, "api.shops.callback", ShopCalls.REQUEST_ITEM_PRICE.name(), id[0] + "", id[1] + "");
    }

    public void sendBought(final int[] id, final int amount) {
        this.economy.getRabbit().send("api.shops.receive", ShopCalls.UPDATE_ITEM_BOUGHT.name(), id[0] + "", id[1] + "", amount + "");
    }

    public void sendSold(final int[] id, final int amount) {
        this.economy.getRabbit().send("api.shops.receive", ShopCalls.UPDATE_ITEM_SOLD.name(), id[0] + "", id[1] + "", amount + "");
    }

    public void setPrice(final int[] id, final double price) {
        this.economy.getRabbit().send("api.shops.receive", ShopCalls.UPDATE_ITEM_PRICE.name(), id[0] + "", id[1] + "", price + "");
    }

}
