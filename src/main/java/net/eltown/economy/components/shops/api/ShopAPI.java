package net.eltown.economy.components.shops.api;

import lombok.Getter;
import lombok.SneakyThrows;
import net.eltown.economy.Economy;
import net.eltown.economy.components.shops.data.ShopCalls;
import net.eltown.economy.components.shops.forms.ShopForms;
import net.eltown.economy.components.tinyrabbit.TinyRabbit;

import java.util.function.Consumer;

@Getter
public class ShopAPI {

    private final Economy economy;
    private final ShopForms shopForms;
    private final TinyRabbit rabbit;

    @SneakyThrows
    public ShopAPI(final Economy economy) {
        this.economy = economy;
        this.shopForms = new ShopForms(this);
        this.rabbit = new TinyRabbit("localhost", "Server/Shops");
        this.rabbit.throwExceptions(true);
    }

    public void getCurrentPrice(final int[] id, final int amount, final Consumer<Double> callback) {
        this.rabbit.sendAndReceive((delivery) -> {
            final double price = Double.parseDouble(delivery.getData()[1]);
            callback.accept(price * amount);
        }, "shops.callback", ShopCalls.REQUEST_ITEM_PRICE.name(), id[0] + "", id[1] + "");
    }

    public void sendBought(final int[] id, final int amount) {
        this.rabbit.send("shops.receive", ShopCalls.UPDATE_ITEM_BOUGHT.name(), id[0] + "", id[1] + "", amount + "");
    }

    public void sendSold(final int[] id, final int amount) {
        this.rabbit.send("shops.receive", ShopCalls.UPDATE_ITEM_SOLD.name(), id[0] + "", id[1] + "", amount + "");
    }

    public void setPrice(final int[] id, final double price) {
        this.rabbit.send("shops.receive", ShopCalls.UPDATE_ITEM_PRICE.name(), id[0] + "", id[1] + "", price + "");
    }

}
