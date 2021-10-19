package net.eltown.economy.components.crypto.api;

import cn.nukkit.Player;
import cn.nukkit.Server;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import lombok.RequiredArgsConstructor;
import net.eltown.economy.Economy;
import net.eltown.economy.components.crypto.data.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class CryptoAPI {

    private final Economy plugin;


    public void getWallet(final Consumer<Wallet> callback, final String player) {
        this.plugin.getRabbit().sendAndReceive((delivery) -> {
            final String[] data = delivery.getData();

            callback.accept(new Wallet(data[1],
                    Double.parseDouble(data[2]),
                    Double.parseDouble(data[3]),
                    Double.parseDouble(data[4])
            ));

        }, "api.crypto.callback", CryptoCalls.REQUEST_WALLET.name(), player);
    }

    public void addCrypto(final String player, final CryptoType type, final double amount) {
        CompletableFuture.runAsync(() -> {
            this.getWallet((wallet) -> {
                switch (type) {
                    case CTC:
                        this.plugin.getRabbit().send("api.crypto.receive", CryptoCalls.UPDATE_WALLET.name(),
                                player,
                                "" + (wallet.getCtc() + amount),
                                "" + wallet.getElt(),
                                "" + wallet.getNot()
                        );
                        break;
                    case ELT:
                        this.plugin.getRabbit().send("api.crypto.receive", CryptoCalls.UPDATE_WALLET.name(),
                                player,
                                "" + wallet.getCtc(),
                                "" + (wallet.getElt() + amount),
                                "" + wallet.getNot()
                        );
                        break;
                    case NOT:
                        this.plugin.getRabbit().send("api.crypto.receive", CryptoCalls.UPDATE_WALLET.name(),
                                player,
                                "" + wallet.getCtc(),
                                "" + wallet.getElt(),
                                "" + (wallet.getNot() + amount)
                        );
                        break;
                }
            }, player);
        });
    }

    public void reduceCrypto(final String player, final CryptoType type, final double amount) {
        CompletableFuture.runAsync(() -> {
            this.getWallet((wallet) -> {
                switch (type) {
                    case CTC:
                        this.plugin.getRabbit().send("api.crypto.receive", CryptoCalls.UPDATE_WALLET.name(),
                                player,
                                "" + (wallet.getCtc() - amount),
                                "" + wallet.getElt(),
                                "" + wallet.getNot()
                        );
                        break;
                    case ELT:
                        this.plugin.getRabbit().send("api.crypto.receive", CryptoCalls.UPDATE_WALLET.name(),
                                player,
                                "" + wallet.getCtc(),
                                "" + (wallet.getElt() - amount),
                                "" + wallet.getNot()
                        );
                        break;
                    case NOT:
                        this.plugin.getRabbit().send("api.crypto.receive", CryptoCalls.UPDATE_WALLET.name(),
                                player,
                                "" + wallet.getCtc(),
                                "" + wallet.getElt(),
                                "" + (wallet.getNot() - amount)
                        );
                        break;
                }
            }, player);
        });
    }

    public void getTransactions(final String player, final Consumer<Set<Transaction>> callback) {
        CompletableFuture.runAsync(() -> {
            this.plugin.getRabbit().sendAndReceive((delivery) -> {

                final LinkedHashSet<Transaction> set = new LinkedHashSet<>();

                final String[] data = delivery.getData();
                if (data[1].equalsIgnoreCase("null")) {
                    callback.accept(set);
                    return;
                }

                final String[] transactions = data[1].split("&");

                for (final String trans : transactions) {
                    final String[] tdata = trans.split(">>");

                    final String
                            id = tdata[0],
                            type = tdata[3],
                            from = tdata[4],
                            to = tdata[5];
                    final double
                            amount = Double.parseDouble(tdata[1]),
                            worth = Double.parseDouble(tdata[2]);
                    final int
                            timeLeft = Integer.parseInt(tdata[6]),
                            time = Integer.parseInt(tdata[8]);
                    final boolean
                            completed = Boolean.parseBoolean(tdata[7]);

                    set.add(new Transaction(id, amount, worth, type, from, to, timeLeft, time, completed));
                }

                callback.accept(set);

            }, "api.crypto.callback", CryptoCalls.REQUEST_TRANSACTIONS.name(), player);
        });
    }

    public void getWorth(final Consumer<Worth> worth) {
        this.plugin.getRabbit().sendAndReceive((delivery) -> {
            final String[] data = delivery.getData();

            worth.accept(new Worth(Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3])));
        }, "api.crypto.callback", CryptoCalls.REQUEST_WORTH.name(), "null");
    }

    public void getTransactionPrices(final Consumer<double[]> callback) {
        this.plugin.getRabbit().sendAndReceive((delivery) -> {
            try {
                final String[] data = delivery.getData();
                final double[] prices = new double[3];
                prices[0] = Double.parseDouble(data[1]);
                prices[1] = Double.parseDouble(data[2]);
                prices[2] = Double.parseDouble(data[3]);
                callback.accept(prices);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "api.crypto.callback", CryptoCalls.REQUEST_TRANSFER_PRICES.name(), "null");
    }

    public void createTransfer(final double price, final double amount, final double worth, final CryptoType type, final Player from, final String to, final int time) {
        this.sellCrypto(from, type, amount + price, 0);

        this.plugin.getRabbit().send("api.crypto.receive", CryptoCalls.UPDATE_TRANSFER_CRYPTO.name(),
                type.name(),
                "" + amount,
                "" + (amount * worth),
                "" + from.getName(),
                "" + to,
                "" + time
        );
    }

    @Deprecated /* Nicht verwenden! */
    public void buyCrypto(final Player player, final CryptoType type, final double amount, final double price) {
        CompletableFuture.runAsync(() -> {
            Economy.getAPI().reduceMoney(player, price);
            this.addCrypto(player.getName(), type, amount);
        });
    }

    @Deprecated /* Nicht verwenden! */
    public void sellCrypto(final Player player, final CryptoType type, final double amount, final double earn) {
        CompletableFuture.runAsync(() -> {
            Economy.getAPI().addMoney(player, earn);
            this.reduceCrypto(player.getName(), type, amount);
        });
    }

}
