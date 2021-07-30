package net.eltown.economy.components.bank.api;

import lombok.RequiredArgsConstructor;
import net.eltown.economy.Economy;
import net.eltown.economy.components.bank.data.BankAccount;
import net.eltown.economy.components.bank.data.BankCalls;
import net.eltown.economy.components.bank.data.BankLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class BankAPI {

    private final Economy instance;

    public void createBankAccount(final String owner, final String prefix, final Consumer<String> callbackPassword) {
        this.instance.getRabbit().sendAndReceive(delivery -> {
            switch (BankCalls.valueOf(delivery.getKey().toUpperCase())) {
                case CALLBACK_CREATE_ACCOUNT:
                    callbackPassword.accept(delivery.getData()[1]);
                    break;
            }
        }, "bank.callback", BankCalls.CALLBACK_CREATE_ACCOUNT.name(), owner, prefix);
    }

    public void insertBankLog(final String account, final String title, final String details) {
        this.instance.getRabbit().send("bank.receive", BankCalls.REQUEST_INSERT_LOG.name(), account, title, details);
    }

    public BankAccount getAccount(final String account) {
        final AtomicReference<BankAccount> reference = new AtomicReference<>(null);

        this.instance.getRabbit().sendAndReceive(delivery -> {
            switch (BankCalls.valueOf(delivery.getKey().toUpperCase())) {
                case CALLBACK_GET_BANK_ACCOUNT:
                    final String rawLogs = delivery.getData()[5];
                    final String[] rawFullLog = rawLogs.split("#+#");

                    final List<BankLog> logs = new ArrayList<>();
                    for (final String s : rawFullLog) {
                        final String[] log = s.split(";");
                        logs.add(new BankLog(log[0], log[1], log[2], log[3]));
                    }

                    reference.set(new BankAccount(delivery.getData()[1], delivery.getData()[2], delivery.getData()[3], Double.parseDouble(delivery.getData()[4]), logs));
                    break;
            }
        }, "bank.callback", BankCalls.REQUEST_GET_BANK_ACCOUNT.name(), account);

        return reference.get();
    }

    public void withdrawMoney(final String account, final double amount) {
        this.instance.getRabbit().send("bank.receive", BankCalls.REQUEST_WITHDRAW_MONEY.name(), account, String.valueOf(amount));
    }

    public void depositMoney(final String account, final double amount) {
        this.instance.getRabbit().send("bank.receive", BankCalls.REQUEST_DEPOSIT_MONEY.name(), account, String.valueOf(amount));
    }

    public void setMoney(final String account, final double amount) {
        this.instance.getRabbit().send("bank.receive", BankCalls.REQUEST_SET_MONEY.name(), account, String.valueOf(amount));
    }
}
