package net.eltown.economy.components.api;

import cn.nukkit.Player;
import lombok.RequiredArgsConstructor;
import net.eltown.economy.components.provider.Provider;
import net.eltown.economy.Economy;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class API {

    private final Economy plugin;
    private final Provider provider;

    public void hasAccount(Player player, Consumer<Boolean> callback) {
        this.hasAccount(player.getName(), callback);
    }

    public void hasAccount(String player, Consumer<Boolean> callback) {
        CompletableFuture.runAsync(() -> {
            this.provider.hasAccount(player, callback);
        });
    }

    public void createAccount(Player player, double money) {
        this.createAccount(player.getName(), money);
    }

    public void createAccount(String username, double money) {
        CompletableFuture.runAsync(() -> {
            this.provider.createAccount(username, money);
        });
    }

    public void getMoney(UUID uuid, Consumer<Double> callback) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.getMoney(name, callback);
    }

    public void getMoney(Player player, Consumer<Double> callback) {
        this.getMoney(player.getName(), callback);
    }

    public void getMoney(String username, Consumer<Double> callback) {
        CompletableFuture.runAsync(() -> {
            this.provider.getMoney(username, callback);
        });
    }

    public void setMoney(UUID uuid, double money) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.setMoney(name, money);
    }

    public void setMoney(Player player, double money) {
        this.setMoney(player.getName(), money);
    }

    public void setMoney(String username, double money) {
        CompletableFuture.runAsync(() -> {
            this.provider.setMoney(username, money);
        });
    }


    public void addMoney(UUID uuid, double money) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.addMoney(name, money);
    }

    public void addMoney(Player player, double money) {
        this.addMoney(player.getName(), money);
    }

    public void addMoney(String username, double money) {
        CompletableFuture.runAsync(() -> {
            this.provider.getMoney(username, (current) -> {
                this.provider.setMoney(username, current + money);
            });
        });
    }

    public void reduceMoney(Player player, double money) {
        this.reduceMoney(player.getName(), money);
    }

    public void reduceMoney(UUID uuid, double money) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.reduceMoney(name, money);
    }

    public void reduceMoney(String username, double money) {
        CompletableFuture.runAsync(() -> {
            this.provider.getMoney(username, (current) -> {
                this.provider.setMoney(username, current - money);
            });
        });
    }

    public void getAll(Consumer<Map<String, Double>> callback) {
        CompletableFuture.runAsync(() -> callback.accept(this.provider.getAll()));
    }

    public Map<String, Double> getAll() {
        return this.provider.getAll();
    }

    public String getMonetaryUnit() {
        return this.plugin.getMonetaryUnit();
    }

    public DecimalFormat getMoneyFormat() {
        return this.plugin.getMoneyFormat();
    }

    public double getDefaultMoney() {
        return this.plugin.getDefaultMoney();
    }

}
