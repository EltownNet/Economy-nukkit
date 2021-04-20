package net.eltown.economy.components.provider;

import lombok.RequiredArgsConstructor;
import net.eltown.economy.Economy;
import net.eltown.economy.components.data.CallData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LlamaDevelopment
 * @project MobEarn
 * @website http://llamadevelopment.net/
 */
@RequiredArgsConstructor
public class Provider {

    private final Economy plugin;

    public void close() { }

    public void hasAccount(String id, Consumer<Boolean> callback) {
        this.plugin.getRabbit().sendAndReceive((delivery -> {
            callback.accept(delivery.getData()[1].equalsIgnoreCase("true"));
        }), "economy", CallData.REQUEST_ACCOUNTEXISTS.name(), id);
    }

    public void createAccount(String id, double money) {
        this.plugin.getRabbit().sendAndReceive((delivery -> {}), "economy", CallData.REQUEST_CREATEACCOUNT.name(), id, String.valueOf(money));
    }

    public void getMoney(String id, Consumer<Double> callback) {
        this.plugin.getRabbit().sendAndReceive((delivery -> {
            callback.accept(Double.parseDouble(delivery.getData()[1]));
        }), "economy", CallData.REQUEST_GETMONEY.name(), id);
    }

    public void setMoney(String id, double money) {
        this.plugin.getRabbit().sendAndReceive((delivery -> {}), "economy", CallData.REQUEST_SETMONEY.name(), id, String.valueOf(money));
    }

    public Map<String, Double> getAll() {
        return new HashMap<>();
        //final Map<String, Double> map = new HashMap<>();
        //this.collection.find().getAll().forEach((udoc) -> map.put(udoc.getString("_id"), udoc.getDouble("money")));
        //return map;
    }
}
