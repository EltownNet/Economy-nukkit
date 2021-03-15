package net.lldv.llamaeconomy.components.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.data.CallData;
import net.lldv.llamaeconomy.components.data.MessageCall;

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

    private final LlamaEconomy plugin;

    private MessageCall messageCall;

    public void init() {
        this.messageCall = new MessageCall(this.plugin);
    }

    public void close() { }

    public void hasAccount(String id, Consumer<Boolean> callback) {
        this.messageCall.createCall((c, s) -> {
            callback.accept(s[1].equalsIgnoreCase("true"));
        }, CallData.REQUEST_ACCOUNTEXISTS, id);
    }

    public void createAccount(String id, double money) {
        this.messageCall.createCall((c, s) -> { }, CallData.REQUEST_CREATEACCOUNT, id, money + "");
    }

    public void getMoney(String id, Consumer<Double> callback) {
        this.messageCall.createCall((c, s) -> {
            callback.accept(Double.parseDouble(s[1]));
        }, CallData.REQUEST_GETMONEY, id);
    }

    public void setMoney(String id, double money) {
        this.messageCall.createCall((c, s) -> {

        }, CallData.REQUEST_SETMONEY, id, money + "");
    }

    public Map<String, Double> getAll() {
        return new HashMap<>();
        //final Map<String, Double> map = new HashMap<>();
        //this.collection.find().getAll().forEach((udoc) -> map.put(udoc.getString("_id"), udoc.getDouble("money")));
        //return map;
    }
}
