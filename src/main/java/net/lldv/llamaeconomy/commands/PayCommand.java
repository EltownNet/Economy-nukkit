package net.lldv.llamaeconomy.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.language.Language;

import java.util.concurrent.CompletableFuture;

public class PayCommand extends PluginCommand<LlamaEconomy> {

    public PayCommand(LlamaEconomy owner, ConfigSection section) {
        super(section.getString("Name"), owner);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        final String[] params = section.getString("Parameters").split(";");
        addCommandParameters("default", new CommandParameter[]{
                new CommandParameter(params[0], CommandParamType.STRING, false),
                new CommandParameter(params[1], CommandParamType.FLOAT, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        CompletableFuture.runAsync(() -> {
            if (sender.isPlayer()) {
                if (args.length >= 2) {
                    Player payer = (Player) sender;

                    LlamaEconomy.getAPI().getMoney(payer, (senderMoney) -> {
                        try {
                            double toPay = Double.parseDouble(args[1]);

                            if (toPay > senderMoney) {
                                payer.sendMessage(Language.get("pay-not-enough-money"));
                                return;
                            }

                            if (toPay < 0) {
                                sender.sendMessage(Language.get("invalid-amount"));
                                return;
                            }

                            String target = args[0];
                            Player playerTarget = getPlugin().getServer().getPlayer(target);
                            if (playerTarget != null) target = playerTarget.getName();

                            if (target.equals(sender.getName())) return;

                            String finalTarget = target;
                            LlamaEconomy.getAPI().hasAccount(target, (has) -> {
                                if (!has) {
                                    payer.sendMessage(Language.get("not-registered", finalTarget));
                                    return;
                                }

                                LlamaEconomy.getAPI().reduceMoney(payer.getName(), toPay);
                                LlamaEconomy.getAPI().addMoney(finalTarget, toPay);

                                payer.sendMessage(Language.get("you-paid", finalTarget, getPlugin().getMonetaryUnit(), this.getPlugin().getMoneyFormat().format(toPay)));

                                if (playerTarget != null) {
                                    playerTarget.sendMessage(Language.get("paid-you", payer.getName(), this.getPlugin().getMonetaryUnit(), this.getPlugin().getMoneyFormat().format(toPay)));
                                }
                            });
                        } catch (Exception ex) {
                            sender.sendMessage(Language.get("invalid-amount"));
                            ex.printStackTrace();
                        }
                    });

                } else sender.sendMessage(Language.get("usage", getUsage()));
            }
        });

        return false;
    }
}
