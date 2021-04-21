package net.eltown.economy.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.eltown.economy.components.math.SortPlayer;
import net.eltown.economy.Economy;
import net.eltown.economy.components.language.Language;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TopMoneyCommand extends PluginCommand<Economy> {

    public TopMoneyCommand(Economy owner, ConfigSection section) {
        super(section.getString("Name"), owner);
        this.setDescription(section.getString("Description"));
        this.setUsage(section.getString("Usage"));
        this.setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        final String param = section.getString("Parameters");
        this.addCommandParameters("default", new CommandParameter[]{new CommandParameter(param, CommandParamType.INT, true)});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Economy.getAPI().getAll(all -> {
            List<SortPlayer> sortPlayers = new ArrayList<>();
            all.forEach((string, money) -> sortPlayers.add(new SortPlayer(string, money)));
            sortPlayers.sort(Comparator.comparing(SortPlayer::getMoney).reversed());

            int maxPages = sortPlayers.size() / 5;
            if (maxPages * 5 == sortPlayers.size()) maxPages--; // idk why
            int page = 0;

            try {
                if (args.length >= 1) {
                    int tPage = Integer.parseInt(args[0]) - 1;
                    page = Math.min(tPage, maxPages);
                }
            } catch (Exception ex) {
                sender.sendMessage(Language.get("topmoney-invalid"));
                return;
            }

            sender.sendMessage(Language.getNP("topmoney-header"));

            int startFromIndex = page * 5;
            for (int i = 0; i < 5; i++) {
                int at = startFromIndex + i;
                if (sortPlayers.size() - 1 >= at) {
                    SortPlayer sortPlayer = sortPlayers.get(at);
                    sender.sendMessage(Language.getNP("topmoney-player", at + 1, sortPlayer.getName(), getPlugin().getMonetaryUnit(), getPlugin().getMoneyFormat().format(sortPlayer.getMoney())));
                }
            }

            sender.sendMessage("\n" + Language.getNP("topmoney-siteinfo", page + 1, maxPages + 1));

            sender.sendMessage(Language.getNP("topmoney-footer"));
        });
        return false;
    }
}
