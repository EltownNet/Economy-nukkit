package net.eltown.economy.components.bank.api;

import lombok.RequiredArgsConstructor;
import net.eltown.economy.Economy;
import net.eltown.economy.components.bank.data.BankAccount;
import net.eltown.economy.components.bank.data.BankCalls;
import net.eltown.economy.components.bank.data.BankLog;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class BankAPI {

    private final Economy instance;

    public void createBankAccount(final String owner, final String prefix, final BiConsumer<String, String> callbackData) {
        this.instance.getRabbit().sendAndReceive(delivery -> {
            switch (BankCalls.valueOf(delivery.getKey().toUpperCase())) {
                case CALLBACK_CREATE_ACCOUNT:
                    callbackData.accept(delivery.getData()[1], delivery.getData()[2]);
                    break;
            }
        }, "api.bank.callback", BankCalls.REQUEST_CREATE_ACCOUNT.name(), owner, prefix);
    }

    public void insertBankLog(final String account, final String title, final String details) {
        this.instance.getRabbit().send("api.bank.receive", BankCalls.REQUEST_INSERT_LOG.name(), account, title, details);
    }

    public void getAccount(final String account, final Consumer<BankAccount> bankAccountConsumer) {
        this.instance.getRabbit().sendAndReceive(delivery -> {
            switch (BankCalls.valueOf(delivery.getKey().toUpperCase())) {
                case CALLBACK_GET_BANK_ACCOUNT:
                    final String rawLogs = delivery.getData()[6];
                    final String[] rawFullLog = rawLogs.split("--");

                    final List<BankLog> logs = new ArrayList<>();
                    for (final String s : rawFullLog) {
                        final String[] log = s.split(";");
                        logs.add(new BankLog(log[0], log[1], log[2], log[3]));
                    }

                    bankAccountConsumer.accept(new BankAccount(delivery.getData()[1], delivery.getData()[2], delivery.getData()[3], delivery.getData()[4], Double.parseDouble(delivery.getData()[5]), logs));
                    break;
            }
        }, "api.bank.callback", BankCalls.REQUEST_GET_BANK_ACCOUNT.name(), account);
    }

    public void withdrawMoney(final String account, final double amount) {
        this.instance.getRabbit().send("api.bank.receive", BankCalls.REQUEST_WITHDRAW_MONEY.name(), account, String.valueOf(amount));
    }

    public void depositMoney(final String account, final double amount) {
        this.instance.getRabbit().send("api.bank.receive", BankCalls.REQUEST_DEPOSIT_MONEY.name(), account, String.valueOf(amount));
    }

    public void setMoney(final String account, final double amount) {
        this.instance.getRabbit().send("api.bank.receive", BankCalls.REQUEST_SET_MONEY.name(), account, String.valueOf(amount));
    }

    public void changePassword(final String account, final String password) {
        this.instance.getRabbit().send("api.bank.receive", BankCalls.REQUEST_CHANGE_PASSWORD.name(), account, password);
    }

    public void changeDisplayName(final String account, final String displayName) {
        this.instance.getRabbit().send("api.bank.receive", BankCalls.REQUEST_CHANGE_DISPLAY_NAME.name(), account, displayName);
    }
}
