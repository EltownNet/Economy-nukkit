package net.eltown.economy.components.economy.provider;

import lombok.RequiredArgsConstructor;
import net.eltown.economy.Economy;
import net.eltown.economy.components.economy.data.CallData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        }), "api.economy.callback", CallData.REQUEST_ACCOUNTEXISTS.name(), id);
    }

    public void createAccount(String id, double money) {
        this.plugin.getRabbit().sendAndReceive((delivery -> {}), "api.economy.callback", CallData.REQUEST_CREATEACCOUNT.name(), id, String.valueOf(money));
    }

    public void getMoney(String id, Consumer<Double> callback) {
        this.plugin.getRabbit().sendAndReceive((delivery -> {
            callback.accept(Double.parseDouble(delivery.getData()[1]));
        }), "api.economy.callback", CallData.REQUEST_GETMONEY.name(), id);
    }

    public void setMoney(String id, double money) {
        this.plugin.getRabbit().sendAndReceive((delivery -> {}), "api.economy.callback", CallData.REQUEST_SETMONEY.name(), id, String.valueOf(money));
    }

    public void getAll(Consumer<Map<String, Double>> callback) {
        final Map<String, Double> map = new HashMap<>();
        this.plugin.getRabbit().sendAndReceive((delivery -> {

            List<String> list = Arrays.asList(delivery.getData());
            list.forEach((str) -> {
                if (!str.equals(delivery.getKey().toLowerCase())) {
                    map.put(str.split(":")[0], Double.parseDouble(str.split(":")[1]));
                }
            });
            callback.accept(map);

        }), "api.economy.callback", CallData.REQUEST_GETALL.name());
    }
}
