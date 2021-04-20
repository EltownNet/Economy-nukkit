package net.eltown.economy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.eltown.economy.Economy;
import net.eltown.economy.components.language.Language;

public class MoneyCommand extends PluginCommand<Economy> {

    public MoneyCommand(Economy owner, ConfigSection section) {
        super(section.getString("Name"), owner);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        final String param = section.getString("Parameters");
        addCommandParameters("default", new CommandParameter[]{new CommandParameter(param, CommandParamType.STRING, true)});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length >= 1) {
            String target = args[0];
            Player playerTarget = getPlugin().getServer().getPlayer(target);
            if (playerTarget != null) target = playerTarget.getName();

            String finalTarget = target;
            Economy.getAPI().hasAccount(target, (has) -> {
                if (!has) {
                    sender.sendMessage(Language.get("not-registered", finalTarget));
                    return;
                }

                Economy.getAPI().getMoney(finalTarget, (money) -> {
                    sender.sendMessage(Language.get("money-other", finalTarget, getPlugin().getMonetaryUnit(), getPlugin().getMoneyFormat().format(money)));
                });
            });
        } else {
            if (sender.isPlayer()) {
                Economy.getAPI().getMoney(sender.getName(), (money) -> {
                    sender.sendMessage(Language.get("money", getPlugin().getMonetaryUnit(), getPlugin().getMoneyFormat().format(money)));
                });
            }
        }
        return false;
    }
}
