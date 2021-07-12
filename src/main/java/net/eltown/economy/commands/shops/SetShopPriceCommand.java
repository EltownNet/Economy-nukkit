package net.eltown.economy.commands.shops;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import net.eltown.economy.Economy;

public class SetShopPriceCommand extends PluginCommand<Economy> {

    public SetShopPriceCommand(final Economy owner) {
        super("setshopprice", owner);
        this.setPermission("economy.command.setshopprice");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission(this.getPermission())) return true;
        if (args.length == 3) {
            try {
                final int id = Integer.parseInt(args[0]);
                final int subId = Integer.parseInt(args[1]);
                final double price = Double.parseDouble(args[2]);

                Economy.getShopAPI().setPrice(new int[]{id, subId}, price);
                sender.sendMessage("Der Preis für das Item " + id + ":" + subId + " wurde auf $" + price + " gesetzt.");
            } catch (final Exception e) {
                sender.sendMessage("Fehlerhafte Angaben.");
            }
        }
        return true;
    }
}
