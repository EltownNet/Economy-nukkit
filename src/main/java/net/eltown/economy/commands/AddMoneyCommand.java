package net.eltown.economy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.eltown.economy.Economy;
import net.eltown.economy.components.language.Language;

import java.util.concurrent.CompletableFuture;

public class AddMoneyCommand extends PluginCommand<Economy> {

    public AddMoneyCommand(Economy owner, ConfigSection section) {
        super(section.getString("Name"), owner);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        setPermission(section.getString("Permission"));
        final String[] params = section.getString("Parameters").split(";");
        addCommandParameters("default", new CommandParameter[]{
                new CommandParameter(params[0], CommandParamType.STRING, false),
                new CommandParameter(params[1], CommandParamType.FLOAT, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.hasPermission(getPermission())) return false;
        CompletableFuture.runAsync(() -> {
            if (args.length >= 2) {
                try {
                    String target = args[0];
                    Player playerTarget = getPlugin().getServer().getPlayer(target);
                    if (playerTarget != null) target = playerTarget.getName();

                    String finalTarget = target;
                    Economy.getAPI().hasAccount(target, (has) -> {
                        if (!has) {
                            sender.sendMessage(Language.get("not-registered", finalTarget));
                            return;
                        }

                        double amt = Double.parseDouble(args[1]);

                        if (amt < 0) {
                            sender.sendMessage(Language.get("invalid-amount"));
                            return;
                        }

                        Economy.getAPI().addMoney(finalTarget, amt);
                        sender.sendMessage(Language.get("added-money", finalTarget, getPlugin().getMonetaryUnit(), this.getPlugin().getMoneyFormat().format(amt)));
                    });
                } catch (NumberFormatException ex) {
                    sender.sendMessage(Language.get("invalid-amount"));
                }
            } else sender.sendMessage(Language.get("usage", getUsage()));
        });
        return false;
    }
}
