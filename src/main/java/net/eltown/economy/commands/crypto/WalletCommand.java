package net.eltown.economy.commands.crypto;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import net.eltown.economy.Economy;
import net.eltown.economy.components.crypto.api.CryptoAPI;
import net.eltown.economy.components.crypto.data.CryptoType;
import net.eltown.economy.components.crypto.data.Transaction;
import net.eltown.economy.components.crypto.data.Wallet;
import net.eltown.economy.components.crypto.data.Worth;
import net.eltown.economy.components.economy.language.Language;
import net.eltown.economy.components.forms.custom.CustomForm;
import net.eltown.economy.components.forms.modal.ModalForm;
import net.eltown.economy.components.forms.simple.SimpleForm;

import java.util.Random;

public class WalletCommand extends PluginCommand<Economy> {

    public WalletCommand(final Economy plugin) {
        super("wallet", plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            this.openWallet(player);
        }
        return false;
    }

    private void openWallet(final Player player) {
        Economy.getCryptoAPI().getWallet((wallet) -> {
            Economy.getCryptoAPI().getWorth((worth) -> {
                new SimpleForm.Builder("Wallet", "Hier siehst du deine Wallet mit all deinen Kryptowährungen.")
                        .addButton(new ElementButton("§f" + formatBalance(wallet.getCtc()) + " CTC\n§8~ §e" + formatBalance(wallet.getCtc() * worth.getCtc()) + "$"), (p) -> this.openCrypto(p, CryptoType.CTC, wallet.getCtc(), worth.getCtc()))
                        .addButton(new ElementButton("§f" + formatBalance(wallet.getElt()) + " ELT\n§8~ §e" + formatBalance(wallet.getElt() * worth.getElt()) + "$"), (p) -> this.openCrypto(p, CryptoType.ELT, wallet.getElt(), worth.getElt()))
                        .addButton(new ElementButton("§f" + formatBalance(wallet.getNot()) + " NOT\n§8~ §e" + formatBalance(wallet.getNot() * worth.getNot()) + "$"), (p) -> this.openCrypto(p, CryptoType.NOT, wallet.getNot(), worth.getNot()))
                        .addButton(new ElementButton("§8» §fTransaktionen"), this::openTransactions)
                        .build().send(player);
            });
        }, player.getName());
    }

    private void openTransactions(final Player player) {
        Economy.getCryptoAPI().getTransactions(player.getName(), (transactions) -> {
            final SimpleForm.Builder builder = new SimpleForm.Builder("§7» §fTransaktionen", "Hier siehst du die letzten Transaktion und Transaktionen, die noch bearbeitet werden.");

            builder.addButton(new ElementButton("§7» §cZurück"), this::openWallet);

            transactions.forEach((t) -> {
                builder.addButton(new ElementButton(
                        (t.getFrom().equalsIgnoreCase(player.getName()) ? "§c-" : "§a+") + formatBalance(t.getAmount()) + " " + t.getType() + "\n"
                                + this.getProgressString(t.getMinutes(), t.getMinutesLeft())
                ), (p) -> {
                    new SimpleForm.Builder(t.getId(),
                            "Status: " + (t.isCompleted() ? "§aAbgeschlossen" : "§eIn Bearbeitung")
                                    + "\n§rMenge: " + (t.getFrom().equalsIgnoreCase(player.getName()) ? "§c-" : "§a+") + formatBalance(t.getAmount()) + " " + t.getType()
                                    + "\n§r" + (t.getFrom().equalsIgnoreCase(player.getName()) ? "An: " + t.getTo() : "Von: " + t.getFrom())
                    ).addButton(new ElementButton("§8» §4Zurück"), this::openTransactions)
                            .build().send(player);
                });
            });

            builder.build().send(player);

        });
    }

    private String getProgressString(final int time, final int timeLeft) {
        if (timeLeft == 0) return "§aAbgeschlossen";
        final double percent = 100 - (((float) timeLeft / (float) time) * 100);

        final long green = Math.round(percent);

        final StringBuilder builder = new StringBuilder();

        for (int i = 1; i <= 100; i++) {
            builder.append(i <= green ? "§2|" : "§7|");
        }

        return "§8[" + builder.toString() + "§8]";
    }

    private void openCrypto(final Player player, final CryptoType type, final double balance, final double worth) {
        new SimpleForm.Builder(type.name(), "Verwalte hier deine " + type.name() + ".\nDu hast zurzeit §9" + formatBalance(balance) + " " + type.name() + "§r\nDiese sind zurzeit insgesamt §a$" + this.getPlugin().getMoneyFormat().format(balance * worth) + "§r Wert.")
                .addButton(new ElementButton("§7» §f" + type.name() + " Kaufen"), (p) -> this.buyCrypto(player, type, worth))
                .addButton(new ElementButton("§7» §f" + type.name() + " Verkaufen"), (p) -> this.sellCrypto(player, type, worth, balance))
                .addButton(new ElementButton("§7» §f" + type.name() + " Überweisen"), (p) -> this.preTransferCrypto(player, type, worth, balance))
                .addButton(new ElementButton("§7» §4Zurück"), this::openWallet)
                .build().send(player);
    }

    private void preTransferCrypto(final Player player, final CryptoType type, final double worth, final double balance) {
        Economy.getCryptoAPI().getTransactionPrices((doubles) -> {
            try {

                final Random random = new Random();

                new SimpleForm.Builder("§8» §f" + type.name() + " Überweisen", "Kryptowährungen zu versenden ist mit Gebühren verbunden, da sie erst durch die Eltownchain durch müssen. Bitte wähle unten die gewünschte Geschwindigkeit des Transfers.\n§4Achtung: §rDie " + type.name() + " werden von deinen Transferbetrag abgezogen.")
                        .addButton(new ElementButton("§8» §fLangsam (3-6h)" +
                                "\n" + formatBalance(doubles[0] / worth) + " " + type.name() + " §8~ §e" + formatBalance(doubles[0])
                        ), (p) -> {
                            this.transferCrypto(p, type, worth, balance, doubles[0] / worth, random.nextInt(180) + 180);
                        })
                        .addButton(new ElementButton("§8» §fNormal (1-3h)" +
                                "\n" + formatBalance(doubles[1] / worth) + " " + type.name() + " §8~ §e" + formatBalance(doubles[1])
                        ), (p) -> {
                            this.transferCrypto(p, type, worth, balance, doubles[1] / worth, random.nextInt(120) + 60);
                        })
                        .addButton(new ElementButton("§8» §fSchnell (1-10min)" +
                                "\n" + formatBalance(doubles[2] / worth) + " " + type.name() + " §8~ §e" + formatBalance(doubles[2])
                        ), (p) -> {
                            this.transferCrypto(p, type, worth, balance, doubles[2] / worth, random.nextInt(9) + 1);
                        }).build().send(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    private void transferCrypto(final Player player, final CryptoType type, final double worth, final double balance, final double cost, final int time) {
        new CustomForm.Builder("§8» §f" + type.name() + " Überweisen")
                .addElement(new ElementLabel("Gebe an, wie viel " + type.name() + " und an wen du diese überweisen möchtest.\nDu hast zurzeit §a" + formatBalance(balance - cost) + " " + type.name() + "§e*§r.\n\n" + "§e*§r §c-" + formatBalance(cost) + " " + type.name() + " §rTransfergebühren."))
                .addElement(new ElementInput("Empfänger", "Steve"))
                .addElement(new ElementInput("Betrag (Min. 0.0001; Max. " + formatBalance(balance - cost) + ")", formatBalance(balance / 2)))
                .onSubmit((p, f) -> {
                    try {
                        String preUser = f.getInputResponse(1);

                        final Player preTarget = Server.getInstance().getPlayer(preUser);
                        if (preTarget != null) preUser = preTarget.getName();

                        final String user = preUser;

                        final double amount = Double.parseDouble(f.getInputResponse(2).replace(",", "."));

                        Economy.getAPI().hasAccount(user, (has) -> {
                            if (has) {
                                if ((amount + cost) <= balance && amount >= 0.0001) {
                                    new ModalForm.Builder("§7» §f" + type.name() + " Überweisen",
                                            type.name() + "-Überweisung an §9" + user + "§r."
                                                    + "\n\nMenge: §9" + formatBalance(amount) + " " + type.name()
                                                    + "\n§rTransfergebühren: §9" + formatBalance(cost) + " " + type.name()
                                                    + "\n§rWert: §a$" + this.getPlugin().getMoneyFormat().format(worth * amount)
                                                    + "\n\n§r§lZu zahlen: §r§9" + formatBalance(amount + cost) + " " + type.name(),
                                            "§7» §aJa", "§7» §cNein")
                                            //"Möchtest du wirklich §a" + formatBalance(amount) + " " + type.name() + "§r an §a" + user + "§r überweisen?\n\nDies entspricht zurzeit §a" + Economy.getAPI().getMoneyFormat().format(worth * amount) + "$§r. Es kostet dich insgesamt §a" + formatBalance(amount + cost) + " " + type.name() + " (Betrag + Gebühren)§r.", )
                                            .onYes((pp) -> {
                                                Economy.getCryptoAPI().createTransfer(cost, amount, worth, type, pp, user, time);
                                                p.sendMessage(Language.get("crypto.sent", formatBalance(amount), type, user));

                                                if (preTarget != null) {
                                                    preTarget.sendMessage(Language.get("crypto.get", pp.getName(), formatBalance(amount), type.name()));
                                                }
                                            })
                                            .onNo((pp) -> {

                                            }).build().send(player);
                                } else p.sendMessage(Language.get("invalid.amount"));
                            } else p.sendMessage(Language.get("not-registered", user));
                        });


                    } catch (final Exception ex) {
                        p.sendMessage(Language.get("invalid.amount"));
                    }
                }).build().send(player);
    }

    private void sellCrypto(final Player player, final CryptoType type, final double worth, final double balance) {
        new CustomForm.Builder(("§7» §f" + type.name() + " Verkaufen"))
                .addElement(new ElementLabel("Wie viel §9" + type.name() + "§r möchtest du verkaufen?\n\n"
                        + "§lWertetabelle:§r\n"
                        + "§91 " + type.name() + " §r= §a$" + this.getPlugin().getMoneyFormat().format(1 * worth)
                        + "\n§90,1 " + type.name() + " §r= §a$" + this.getPlugin().getMoneyFormat().format(0.1 * worth)
                        + "\n§90,01 " + type.name() + " §r= §a$" + this.getPlugin().getMoneyFormat().format(0.01 * worth)
                        + "\n§90,001 " + type.name() + " §r= §a$" + this.getPlugin().getMoneyFormat().format(0.001 * worth)
                        + "\n§90,0001 " + type.name() + " §r= §a$" + this.getPlugin().getMoneyFormat().format(0.0001 * worth)
                ))
                .addElement(new ElementInput("Betrag (Min. 0,0001; Max. " + formatBalance(balance) + ")", "0,0001", "0,0001"))
                .onSubmit((p, f) -> {
                    try {
                        final double amount = Double.parseDouble(f.getInputResponse(1).replace(",", "."));

                        if (amount >= 0.0001) {
                            if (amount <= balance) {
                                final double earn = amount * worth;
                                new ModalForm.Builder("§7» §f" + type.name() + " Kaufen",
                                        "Abschließen des Verkaufsvorgangs."
                                                + "\n\nWährung: §9" + type.name()
                                                + "§r\nMenge: §9" + formatBalance(amount)
                                                + "§r\nPreis: §a$" + this.getPlugin().getMoneyFormat().format(earn)
                                                + "§r\n\n§lDu erhälst: §r§a$" + this.getPlugin().getMoneyFormat().format(earn)
                                        ,
                                        //"Möchtest du wirklich " + formatBalance(amount) + " " + type.name() + " für " + this.getPlugin().getMoneyFormat().format(earn) + "$ verkaufen?",
                                        "§7» §aJa",
                                        "§7» §cNein"
                                ).onYes((p1) -> {
                                    Economy.getCryptoAPI().sellCrypto(player, type, amount, earn);
                                    player.sendMessage(Language.get("crypto.sold", this.formatBalance(amount), type.name(), formatBalance(earn), Economy.getAPI().getMonetaryUnit()));
                                }).onNo((p1) -> {

                                }).build().send(player);


                            } else player.sendMessage(Language.get("not.enough.crypto", type.name()));
                        } else player.sendMessage(Language.get("invalid.amount"));
                    } catch (Exception ex) {
                        player.sendMessage(Language.get("invalid.amount"));
                    }
                })
                .build().send(player);
    }

    private void buyCrypto(final Player player, final CryptoType type, final double worth) {
        new CustomForm.Builder(("§7» §f" + type.name() + " Kaufen"))
                .addElement(new ElementLabel("Wie viel §9" + type.name() + "§r möchtest du kaufen?\n\n"
                        + "§lWertetabelle:§r\n"
                        + "§a$10 §r= §9" + formatBalance(10 / worth) + " " + type.name()
                        + "\n§a$100 §r= §9" + formatBalance(100 / worth) + " " + type.name()
                        + "\n§a$1.000 §r= §9" + formatBalance(1000 / worth) + " " + type.name()
                        + "\n§a$10.000 §r= §9" + formatBalance(10000 / worth) + " " + type.name()
                ))
                .addElement(new ElementInput("Betrag (Min. 0,0001)", "0,0001", "0,0001"))
                .onSubmit((p, f) -> {
                    try {
                        final double amount = Double.parseDouble(f.getInputResponse(1).replace(",", "."));

                        if (amount >= 0.0001) {

                            final double price = amount * worth;
                            Economy.getAPI().getMoney(player, (bal) -> {
                                if (price <= bal) {
                                    new ModalForm.Builder("§7» §f" + type.name() + " Kaufen",
                                            "Abschließen des Kaufvorgangs.\n\nWährung: §9" + type.name()
                                                    + "\n§rMenge: §9" + this.formatBalance(amount)
                                                    + "\n§rPreis: §a$" + this.getPlugin().getMoneyFormat().format(price)
                                                    + "\n\n§r§lZu zahlen: §r§a$" + this.getPlugin().getMoneyFormat().format(price),//"Möchtest du wirklich " + formatBalance(amount) + " " + type.name() + " für " + this.getPlugin().getMoneyFormat().format(price) + "$ kaufen?",
                                            "§7» §aJa",
                                            "§7» §cNein"
                                    ).onYes((p1) -> {
                                        Economy.getCryptoAPI().buyCrypto(player, type, amount, price);
                                        player.sendMessage(Language.get("crypto.bought", this.formatBalance(amount), type.name(), price, Economy.getAPI().getMonetaryUnit()));
                                    }).onNo((p1) -> {

                                    }).build().send(player);
                                } else player.sendMessage("§8» §fCrypto §8| §cDu hast nicht genügend Geld.");
                            });

                        } else player.sendMessage("§8» §fCrypto §8| §cUngültiger Betrag.");

                    } catch (Exception ex) {
                        player.sendMessage("§8» §fCrypto §8| §cUngültiger Betrag.");
                    }
                })
                .build().send(player);
    }

    private String formatBalance(final double balance) {
        return this.getPlugin().getCryptoFormat().format(balance);
    }
}
